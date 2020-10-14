package com.quickbirdstudios.surveykit.backend.views.questions

import android.annotation.SuppressLint
import android.content.Context
import androidx.lifecycle.Lifecycle
import com.quickbirdstudios.surveykit.AnswerFormat
import com.quickbirdstudios.surveykit.StepIdentifier
import com.quickbirdstudios.surveykit.backend.address.AddressSuggestionProvider
import com.quickbirdstudios.surveykit.backend.views.question_parts.LocationPickerPart
import com.quickbirdstudios.surveykit.backend.views.step.QuestionView
import com.quickbirdstudios.surveykit.result.QuestionResult
import com.quickbirdstudios.surveykit.result.question_results.LocationQuestionResult

@SuppressLint("ViewConstructor")
internal class LocationPickerQuestionView(
    context: Context,
    id: StepIdentifier,
    isOptional: Boolean,
    title: String?,
    text: String?,
    nextButtonText: String,
    private val lifecycle: Lifecycle?,
    private val addressProvider: AddressSuggestionProvider?,
    private val answerFormat: AnswerFormat.LocationAnswerFormat,
    private val preselected: AnswerFormat.LocationAnswerFormat.Location?
) : QuestionView(context, id, isOptional, title, text, nextButtonText) {

    //region Members

    private lateinit var locationPickerPart: LocationPickerPart

    //endregion

    //region Overrides

    override fun createResults(): QuestionResult = LocationQuestionResult(
        id = id,
        startDate = startDate,
        stringIdentifier = if (
            locationPickerPart.selected.latitude == 0.0 &&
            locationPickerPart.selected.longitude == 0.0
        ) {
            ""
        } else {
            "${locationPickerPart.selected.latitude},${locationPickerPart.selected.longitude}"
        },
        answer = locationPickerPart.selected.toLocation()
    )

    override fun isValidInput(): Boolean = true

    override fun setupViews() {
        super.setupViews()

        if (lifecycle == null) {
            throw RuntimeException(
                "Location typed question steps need to attach an Lifecycle." +
                    " Please pass an Lifecycle when instated on constructor."
            )
        }

        if (addressProvider == null) {
            throw RuntimeException(
                "Location typed question steps need to an AddressSuggestionProvider." +
                    "Please pass an AddressSuggestionProvider when instated on constructor."
            )
        }

        locationPickerPart = content.add(LocationPickerPart(context, lifecycle, addressProvider))

        preselected?.let { locationPickerPart.selected = it.toSelected() }
    }

    //endregion

    //region Private API

    private fun AnswerFormat.LocationAnswerFormat.Location.toSelected(): LocationPickerPart.Selection =
        LocationPickerPart.Selection(latitude = this.latitude, longitude = this.longitude)

    private fun LocationPickerPart.Selection.toLocation(): AnswerFormat.LocationAnswerFormat.Location =
        AnswerFormat.LocationAnswerFormat.Location(
            latitude = this.latitude ?: 0.0,
            longitude = this.longitude ?: 0.0
        )

    //endregion
}
