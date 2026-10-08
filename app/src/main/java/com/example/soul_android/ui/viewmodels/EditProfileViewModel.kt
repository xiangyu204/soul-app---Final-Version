package com.example.soul_android.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.soul_android.data.network.ProfileResponse
import com.example.soul_android.data.network.SoulApiService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody


sealed class EditProfileUiState {

    object Loading :
        EditProfileUiState()


    data class Success(

        val profile:
        ProfileResponse

    ) :
        EditProfileUiState()


    data class Error(

        val message:
        String

    ) :
        EditProfileUiState()
}


class EditProfileViewModel(

    private val api:
    SoulApiService =
        SoulApiService.create()

) : ViewModel() {


    private val _uiState =
        MutableStateFlow<EditProfileUiState>(
            EditProfileUiState.Loading
        )


    val uiState =
        _uiState.asStateFlow()


    // =====================================================
    // 获取 Profile
    // =====================================================

    fun loadProfile(
        username: String
    ) {

        if (
            username.isBlank()
        ) {

            _uiState.value =
                EditProfileUiState.Error(
                    "로그인 정보가 없습니다."
                )

            return
        }


        viewModelScope.launch {

            _uiState.value =
                EditProfileUiState.Loading


            try {

                val response =
                    api.getUserProfile(
                        username
                    )


                if (
                    !response.isSuccessful
                ) {

                    _uiState.value =
                        EditProfileUiState.Error(

                            "프로필 조회 실패: HTTP ${response.code()}"
                        )

                    return@launch
                }


                val profile =
                    response.body()


                if (
                    profile == null
                ) {

                    _uiState.value =
                        EditProfileUiState.Error(
                            "프로필 데이터가 없습니다."
                        )

                    return@launch
                }


                _uiState.value =
                    EditProfileUiState.Success(
                        profile
                    )


            } catch (
                e: Exception
            ) {

                _uiState.value =
                    EditProfileUiState.Error(

                        e.localizedMessage
                            ?: "서버 연결 실패"
                    )
            }
        }
    }


    // =====================================================
    // 保存 Profile
    // =====================================================

    fun saveProfile(

        profile:
        ProfileResponse,

        onSuccess:
            () -> Unit,

        onError:
            (String) -> Unit

    ) {

        viewModelScope.launch {

            try {

                val response =
                    api.updateProfile(
                        profile
                    )


                if (
                    !response.isSuccessful
                ) {

                    val errorText =
                        response
                            .errorBody()
                            ?.string()


                    onError(

                        errorText
                            ?: "저장 실패: HTTP ${response.code()}"
                    )

                    return@launch
                }


                val saved =
                    response.body()
                        ?: profile


                _uiState.value =
                    EditProfileUiState.Success(
                        saved
                    )


                onSuccess()


            } catch (
                e: Exception
            ) {

                onError(

                    e.localizedMessage
                        ?: "저장 실패"
                )
            }
        }
    }


    // =====================================================
    // 上传头像
    // =====================================================

    fun uploadAvatar(

        username:
        String,

        fileBytes:
        ByteArray,

        mimeType:
        String,

        fileName:
        String,

        onSuccess:
            (String) -> Unit,

        onError:
            (String) -> Unit

    ) {

        viewModelScope.launch {

            try {

                // username multipart
                val usernameBody =
                    username
                        .toRequestBody(
                            "text/plain"
                                .toMediaTypeOrNull()
                        )


                // 图片 multipart
                val imageBody =
                    fileBytes
                        .toRequestBody(
                            mimeType
                                .toMediaTypeOrNull()
                        )


                val filePart =
                    MultipartBody
                        .Part
                        .createFormData(
                            "file",
                            fileName,
                            imageBody
                        )


                val response =
                    api.uploadAvatar(
                        usernameBody,
                        filePart
                    )


                if (
                    !response.isSuccessful
                ) {

                    val errorText =
                        response
                            .errorBody()
                            ?.string()


                    onError(

                        errorText
                            ?: "头像上传失败 HTTP ${response.code()}"
                    )

                    return@launch
                }


                val body =
                    response.body()


                if (
                    body == null
                ) {

                    onError(
                        "服务器没有返回头像数据"
                    )

                    return@launch
                }


                if (
                    !body.success
                ) {

                    onError(

                        body.message
                            ?: "头像上传失败"
                    )

                    return@launch
                }


                val avatar =
                    body.avatar


                if (
                    avatar.isNullOrBlank()
                ) {

                    onError(
                        "服务器返回的头像地址为空"
                    )

                    return@launch
                }


                // =================================================
                // 更新当前页面头像
                // =================================================

                val state =
                    _uiState.value


                if (
                    state
                            is EditProfileUiState.Success
                ) {

                    _uiState.value =
                        state.copy(

                            profile =
                                state.profile.copy(

                                    avatar =
                                        avatar
                                )
                        )
                }


                onSuccess(
                    avatar
                )


            } catch (
                e: Exception
            ) {

                onError(

                    e.localizedMessage
                        ?: "头像上传失败"
                )
            }
        }
    }
}