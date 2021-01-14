package com.buddies.generator.usecase

import com.buddies.common.usecase.BaseUseCases
import com.buddies.generator.BuildConfig.ADMIN_EMAIL
import com.buddies.generator.BuildConfig.ADMIN_PASSWORD
import com.buddies.generator.model.NewTag
import com.buddies.generator.util.ClipboardHelper
import com.buddies.generator.util.QRCodeHelper
import com.buddies.security.encryption.Encrypter
import com.buddies.server.api.GeneratorApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GeneratorUseCases(
    private val generatorApi: GeneratorApi,
    private val encrypter: Encrypter,
    private val clipboardHelper: ClipboardHelper,
    private val qrCodeHelper: QRCodeHelper
) : BaseUseCases() {

    suspend fun loginWithAdminCredentials() = request {
        generatorApi.loginWithEmailAndPassword(ADMIN_EMAIL, ADMIN_PASSWORD)
    }

    suspend fun tagValueExists(
        tagValue: String
    ) = request {
        generatorApi.checkTagValueExists(tagValue)
    }

    suspend fun encryptTagValue(
        tagValue: String
    ) = encrypter.encrypt(tagValue)

    suspend fun addNewTag(
        newTag: NewTag
    ) = request {
        generatorApi.addNewPetTag(newTag.value, newTag.encryptedValue)
    }

    suspend fun generateQRCode(
        data: String,
        height: Int,
        width: Int
    ) = withContext(Dispatchers.Default) {
        qrCodeHelper.createQR(data, height, width)
    }

    fun copyToClipboard(text: String) {
        clipboardHelper.saveToClipboard(text)
    }

    fun logoutAdmin() {
        generatorApi.logout()
    }
}