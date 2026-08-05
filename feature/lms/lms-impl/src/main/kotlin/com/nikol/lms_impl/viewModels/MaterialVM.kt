package com.nikol.lms_impl.viewModels

import android.util.Log
import com.nikol.lms.domain.useCase.GetThemeMaterialsUseCase
import com.nikol.lms.domain.useCase.ThemeMaterialParam
import com.nikol.lms.ui.MaterialToUiMapper
import com.nikol.lms_impl.mvi.intent.ThemeMaterialIntent
import com.nikol.lms_impl.mvi.state.ThemeMaterialState
import direct.direct_core.DirectEffect
import direct.direct_core.onSingle
import direct.direct_viewmodel.DirectViewModel
import javax.inject.Inject

typealias ThemeMaterialsStore = DirectViewModel<ThemeMaterialIntent, ThemeMaterialState, DirectEffect>

class MaterialVM @Inject constructor(
    private val id: Int,
    private val materialToUiMapper: MaterialToUiMapper,
    private val getThemeMaterialsUseCase: GetThemeMaterialsUseCase
) : ThemeMaterialsStore() {

    init {
        setIntent(ThemeMaterialIntent.Load)
    }

    override fun createInitialState(): ThemeMaterialState = ThemeMaterialState.Loading

    override fun handleIntents() = intents {
        onSingle<ThemeMaterialIntent.Load> {
            Log.d("MaterialVM", "Intent LOAD received. Starting request...")

            val newState = getThemeMaterialsUseCase(ThemeMaterialParam(id)).fold(
                ifLeft = {
                    Log.e("MaterialVM", "Request FAILED")
                    ThemeMaterialState.Error
                },
                ifRight = { materials ->
                    Log.d(
                        "MaterialVM",
                        "Request SUCCESS. Received ${materials.size} items. Starting mapping..."
                    )

                    val mappedData = materialToUiMapper.map(materials)

                    Log.d("MaterialVM", "Mapping finished successfully. Setting SUCCESS state...")
                    ThemeMaterialState.ThemeMaterialSuccess(mappedData)
                }
            )
            setState { newState }
        }
    }
}