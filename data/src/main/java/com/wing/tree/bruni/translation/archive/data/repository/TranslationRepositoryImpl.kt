package com.wing.tree.bruni.translation.archive.data.repository

import com.wing.tree.bruni.translation.archive.data.BuildConfig
import com.wing.tree.bruni.translation.archive.data.entity.Request
import com.wing.tree.bruni.translation.archive.data.entity.Request.Body
import com.wing.tree.bruni.translation.archive.data.entity.Translation
import com.wing.tree.bruni.translation.archive.data.mapper.TranslationMapper
import com.wing.tree.bruni.translation.archive.domain.enum.Source
import com.wing.tree.bruni.translation.archive.domain.repository.TranslationRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.wing.tree.bruni.translation.archive.data.source.local.TranslationDataSource as LocalDataSource
import com.wing.tree.bruni.translation.archive.data.source.remote.TranslationDataSource as RemoteDataSource
import com.wing.tree.bruni.translation.archive.domain.model.Translation as Model

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
        sourceText: String,
        target: String,
        source: Source
    ): List<Model> {
        return when(source) {
            Source.DEFAULT -> with(localDataSource.all(sourceText, target)) {
                ifEmpty { translate(sourceText, target) }
            }
            else -> translate(sourceText, target)
        }
    }

    private suspend fun translate(sourceText: String, target: String): List<Translation> {
        val body = Body(q = listOf(sourceText), target = target)
        val request = Request(key = BuildConfig.API_KEY, body = body)

        val response = remoteDataSource.translate(request)
        val translations = response.data.translations

        return translations.map {
            Translation(
                detectedSourceLanguage = it.detectedSourceLanguage,
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
}