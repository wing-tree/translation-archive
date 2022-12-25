package com.wing.tree.bruni.inPlaceTranslate.data.repository

import com.wing.tree.bruni.core.constant.EMPTY
import com.wing.tree.bruni.inPlaceTranslate.data.BuildConfig
import com.wing.tree.bruni.inPlaceTranslate.data.entity.Request
import com.wing.tree.bruni.inPlaceTranslate.data.entity.Request.Body
import com.wing.tree.bruni.inPlaceTranslate.data.entity.Translation
import com.wing.tree.bruni.inPlaceTranslate.data.mapper.TranslationMapper
import com.wing.tree.bruni.inPlaceTranslate.domain.enum.DataSource
import com.wing.tree.bruni.inPlaceTranslate.domain.repository.TranslationRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.wing.tree.bruni.inPlaceTranslate.data.source.local.TranslationDataSource as LocalDataSource
import com.wing.tree.bruni.inPlaceTranslate.data.source.remote.TranslationDataSource as RemoteDataSource
import com.wing.tree.bruni.inPlaceTranslate.domain.model.Translation as Model

class TranslationRepositoryImpl @Inject constructor(
    private val localDataSource: LocalDataSource,
    private val remoteDataSource: RemoteDataSource,
) : TranslationRepository {
    private val coroutineScope = CoroutineScope(SupervisorJob())
    private val ioDispatcher = Dispatchers.IO
    private val translationMapper = TranslationMapper()

    override suspend fun all(
        sourceText: String,
        target: String
    ): List<Model> {
        return localDataSource.all(sourceText, target)
    }

    override suspend fun archive(translation: Model) {
        localDataSource.archive(translation.toEntity())
    }

    override suspend fun archiveAll(list: List<Model>) {
        localDataSource.archiveAll(list.map { it.toEntity() })
    }

    override suspend fun translate(
        dataSource: DataSource,
        source: String,
        sourceText: String,
        target: String
    ): List<Model> {
        return when(dataSource) {
            DataSource.DEFAULT -> with(localDataSource.all(sourceText, target)) {
                ifEmpty { translate(sourceText, source, target) }
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
            Translation(
                rowid = it.rowid(source, sourceText, target),
                detectedSourceLanguage = it.detectedSourceLanguage ?: EMPTY,
                source = source,
                sourceText = sourceText,
                target = target,
                translatedText = it.translatedText
            )
        }.also {
            coroutineScope.launch(ioDispatcher) {
                archiveAll(it)
            }
        }
    }

    private fun Model.toEntity() = translationMapper.toEntity(this)

    companion object {
        private const val FORMAT = "text"
    }
}