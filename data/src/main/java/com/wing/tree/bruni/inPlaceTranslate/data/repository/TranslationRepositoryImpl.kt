package com.wing.tree.bruni.inPlaceTranslate.data.repository

import android.icu.util.Calendar
import android.util.Log
import com.wing.tree.bruni.core.constant.EMPTY
import com.wing.tree.bruni.core.constant.ONE
import com.wing.tree.bruni.core.extension.string
import com.wing.tree.bruni.inPlaceTranslate.data.BuildConfig
import com.wing.tree.bruni.inPlaceTranslate.data.entity.History
import com.wing.tree.bruni.inPlaceTranslate.data.entity.Request
import com.wing.tree.bruni.inPlaceTranslate.data.entity.Request.Body
import com.wing.tree.bruni.inPlaceTranslate.data.entity.Translation
import com.wing.tree.bruni.inPlaceTranslate.data.mapper.TranslationMapper
import com.wing.tree.bruni.inPlaceTranslate.data.source.local.HistoryDataSource
import com.wing.tree.bruni.inPlaceTranslate.domain.enum.DataSource
import com.wing.tree.bruni.inPlaceTranslate.domain.repository.TranslationRepository
import com.wing.tree.bruni.inPlaceTranslate.domain.useCase.IncrementCharactersUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.time.Period
import javax.inject.Inject
import com.wing.tree.bruni.inPlaceTranslate.data.source.local.TranslationDataSource as LocalDataSource
import com.wing.tree.bruni.inPlaceTranslate.data.source.remote.TranslationDataSource as RemoteDataSource
import com.wing.tree.bruni.inPlaceTranslate.domain.model.Translation as Model

class TranslationRepositoryImpl @Inject constructor(
    private val historyDataSource: HistoryDataSource,
    private val localDataSource: LocalDataSource,
    private val remoteDataSource: RemoteDataSource,
    private val incrementCharactersUseCase: IncrementCharactersUseCase
) : TranslationRepository {
    private val calendar: Calendar get() = Calendar.getInstance()
    private val translatedAt: Long get() = calendar.timeInMillis
    private val expiredAt: Long get() = calendar.apply {
        add(Calendar.MONTH, expirationPeriod.months)
    }.timeInMillis

    private val ioDispatcher = Dispatchers.IO
    private val coroutineScope = CoroutineScope(SupervisorJob() + ioDispatcher)
    private val tag = "TranslationRepositoryImpl"
    private val translationMapper = TranslationMapper()

    override suspend fun all(
        sourceText: String,
        target: String
    ): List<Model> {
        return localDataSource.all(sourceText, target)
    }

    override suspend fun insert(translation: Model) {
        historyDataSource.insert(translation.toHistory())
        localDataSource.insert(translation.toEntity())
    }

    override suspend fun insertAll(list: List<Model>) {
        historyDataSource.insertAll(list.map { it.toHistory() })
        localDataSource.insertAll(list.map { it.toEntity() })
    }

    override suspend fun translate(
        dataSource: DataSource,
        source: String,
        sourceText: String,
        target: String
    ): List<Model> {
        return when(dataSource) {
            DataSource.DEFAULT -> {
                val translations = try {
                    localDataSource.all(sourceText, target)
                } catch (illegalStateException: IllegalStateException) {
                    Log.e(tag, illegalStateException.message ?: illegalStateException.string)
                    emptyList()
                }

                with(translations.filterNotExpired()) {
                    if (isNotEmpty()) {
                        historyDataSource.insertAll(map { it.toHistory() })
                    }

                    ifEmpty { translate(sourceText, source, target) }
                }
            }
            else -> translate(sourceText, source, target)
        }
    }

    private suspend fun translate(sourceText: String, source: String, target: String): List<Translation> {
        val body = Body(format = FORMAT, q = sourceText, source = source, target = target)
        val request = Request(key = BuildConfig.API_KEY, body = body)

        val response = remoteDataSource.translate(request)
        val translations = response.data.translations

        return translations.map {
            val translatedText = it.translatedText

            coroutineScope.launch {
                incrementCharactersUseCase(translatedText.length)
            }

            Translation(
                rowid = it.rowid(source, sourceText, target),
                detectedSourceLanguage = it.detectedSourceLanguage ?: EMPTY,
                expiredAt = expiredAt,
                source = source,
                sourceText = sourceText,
                target = target,
                translatedText = translatedText
            )
        }.also {
            coroutineScope.launch {
                try {
                    insertAll(it)
                } catch (illegalStateException: IllegalStateException) {
                    Log.e(tag, illegalStateException.message ?: illegalStateException.string)
                }
            }
        }
    }

    private fun Model.toEntity() = translationMapper.toEntity(this)
    private suspend fun Model.toHistory(): History {
        val isFavorite = historyDataSource.isFavorite(rowid) ?: false

        return History(
            rowid = rowid,
            detectedSourceLanguage = detectedSourceLanguage,
            isFavorite = isFavorite,
            source = source,
            sourceText = sourceText,
            target = target,
            translatedAt = translatedAt,
            translatedText = translatedText
        )
    }

    private fun Translation.isExpired(): Boolean {
        val calendar = Calendar.getInstance()
        val timeInMillis = calendar.timeInMillis

        return expiredAt < timeInMillis
    }

    private fun List<Translation>.filterNotExpired() = filterNot { it.isExpired() }

    companion object {
        private const val FORMAT = "text"
        val expirationPeriod: Period = Period.ofMonths(ONE)
    }
}
