package com.example.stripesdemo.domain.interactor.scan

import com.example.stripesdemo.domain.IoDispatcher
import com.example.stripesdemo.domain.interactor.SuspendUseCase
import kotlinx.coroutines.CoroutineDispatcher
import net.stripesapp.mlsretailsoftware.domain.entity.AlternateMatchCodesDomainEntity
import net.stripesapp.mlsretailsoftware.domain.entity.CustomBarcodeDomainEntity
import net.stripesapp.mlsretailsoftware.domain.entity.MatchDomainEntity
import net.stripesapp.mlsretailsoftware.domain.entity.ScanDomainEntity
import net.stripesapp.mlsretailsoftware.domain.entity.ScanFlowStepDomainEntity
import net.stripesapp.mlsretailsoftware.domain.entity.StringOperationDomainEntity
import net.stripesapp.mlsretailsoftware.domain.entity.enums.ScanField
import net.stripesapp.mlsretailsoftware.domain.entity.enums.ScanSource
import net.stripesapp.mlsretailsoftware.domain.entity.enums.SensorFeedback
import net.stripesapp.mlsretailsoftware.domain.entity.isBarcodeStep
import net.stripesapp.mlsretailsoftware.domain.exception.MatchFailedException
import net.stripesapp.mlsretailsoftware.domain.executor.IoDispatcher
import net.stripesapp.mlsretailsoftware.domain.interactor.SuspendUseCase
import net.stripesapp.mlsretailsoftware.domain.repository.ConfigurationsRepository
import net.stripesapp.mlsretailsoftware.domain.repository.MatchRepository
import net.stripesapp.mlsretailsoftware.domain.repository.ScanRepository
import net.stripesapp.mlsretailsoftware.domain.repository.ScannerRepository
import net.stripesapp.mlsretailsoftware.domain.repository.SessionRepository
import net.stripesapp.mlsretailsoftware.domain.repository.ZoneRepository
import javax.inject.Inject


open class SubmitOpenScanProperty @Inject constructor(
    private val scanRepository: ScanRepository,
    private val sessionRepository: SessionRepository,
    private val scannerRepository: ScannerRepository,
    private val configurationsRepository: ConfigurationsRepository,
    @IoDispatcher dispatcher: CoroutineDispatcher
) : SuspendUseCase<ScanDomainEntity?, SubmitOpenScanProperty.Params>(dispatcher) {

    override suspend fun invoke(params: Params): ScanDomainEntity? {
        val zoneCheckIn = zoneRepository.getZoneCheckIn(params.zoneCheckInId)
        val scanFlowStep = configurationsRepository.getScanFlowStepById(
            zoneCheckIn.projectId, params.scanFlowStepId
        )
        val steps = configurationsRepository.getScanFlowSteps(zoneCheckIn.projectId)
        val session = sessionRepository.getAddScansSession(params.zoneCheckInId)!!
        if (params.clearProperties) {
            scanRepository.resetOpenScan(session.id, null)
        }
        val openScan = scanRepository.getOpenScan(session.id)
        val customBarcodes = scanFlowStep.applyCustomBarcodes(params.value).toMutableMap()

        val match = executeMatch(
            matchCode = customBarcodes[ScanField.MatchCode.dbField] ?: params.value,
            zoneCheckInId = params.zoneCheckInId,
            steps = steps,
            scanFlowStep = scanFlowStep
        )


        val allowNotMatched = scanFlowStep.matchAllowNotMatched != false
        val notifyNotMatched = scanFlowStep.matchNotifyNotMatched == true
        if (!params.allowNotMatched && match == null && (!allowNotMatched || notifyNotMatched)) {
            throw MatchFailedException(allowNotMatched)
        }

        val alternateMatchCodes = match?.alternateMatchCodes?.validateMatchCodes(
            step = scanFlowStep,
            steps = steps,
            zoneCheckInId = zoneCheckIn.id,
            allowNotMatched = allowNotMatched,
        )

        val properties = mapOf(scanFlowStep.valueDBFieldName!! to params.value)
            .plus(match?.data ?: emptyMap())
            .plus(customBarcodes)


        return openScan?.let {
            val retainedAlternateStepId =
                if (params.discardMatchCodes) null else openScan.alternateMatchCodesStepId
            val retainedAlternateMatchCodes =
                if (params.discardMatchCodes) null else openScan.alternateMatchCodes

            val submittableProperties =
                steps.filter { step -> step.order <= scanFlowStep.order }
                    .map { step -> step.valueDBFieldName }.toSet()

            val allProperties = openScan.mergedProperties.plus(properties)
            val scanProperties =
                allProperties.filter { entry -> submittableProperties.contains(entry.key) }
            val scanChanges =
                allProperties.filter { entry -> !submittableProperties.contains(entry.key) }

            val scanSource = getScanSource(scanFlowStep, openScan.scanSource)

            val updated = openScan.copy(
                properties =  scanProperties,
                changes = scanChanges,
                alternateMatchCodesStepId = match?.alternateMatchCodes?.let { params.scanFlowStepId }
                    ?: retainedAlternateStepId,
                alternateMatchCodes = alternateMatchCodes ?: retainedAlternateMatchCodes,
                scanSource = scanSource
            )

            scanRepository.save(updated)
            if (params.sendFeedBack) {
                scannerRepository.sendFeedback(SensorFeedback.SUCCESS)
            }

            updated
        }
    }

    private suspend fun executeMatch(
        matchCode: String,
        zoneCheckInId: String,
        steps: List<ScanFlowStepDomainEntity>,
        scanFlowStep: ScanFlowStepDomainEntity
    ): MatchDomainEntity? {
        val zoneCheckIn = zoneRepository.getZoneCheckIn(zoneCheckInId)
        val scanConfiguration =
            configurationsRepository.getScanConfiguration(zoneCheckIn.projectId)!!

        val matchDatabaseName = scanConfiguration.matchDatabaseName
        val matchDatabasePath = scanConfiguration.matchDatabasePath
        val matchDatabaseFilename = "${matchDatabasePath ?: ""}/${matchDatabaseName}"
        val databaseNameForStep = steps.matchDatabasesForSteps(matchDatabaseFilename)

        val match = matchValue(scanFlowStep, matchCode, databaseNameForStep)
        val result = scanFlowStep.validateMatchResult(
            match, zoneCheckIn.zoneCode
        )

        return if (result is MatchValidationResult.Continue) {
            result.match
        } else {
            null //throw MatchFailedException()
        }
    }

    private suspend fun matchValue(
        scanFlowStep: ScanFlowStepDomainEntity,
        matchCode: String,
        databaseNameForStep: Map<String, String>
    ): MatchDomainEntity? {
        val databaseName = databaseNameForStep[scanFlowStep.valueDBFieldName] ?: return null
        return matchRepository.getMatch(databaseName, matchCode, scanFlowStep.getFieldNames())
    }


    private suspend fun AlternateMatchCodesDomainEntity.validateMatchCodes(
        step: ScanFlowStepDomainEntity,
        zoneCheckInId: String,
        steps: List<ScanFlowStepDomainEntity>,
        allowNotMatched: Boolean,
    ): AlternateMatchCodesDomainEntity? {
        val options = if (allowNotMatched) {
            this.options
        } else
            this.options.filter {
            val customBarCodes = step.applyCustomBarcodes(it.matchCode).toMutableMap()
            val match = executeMatch(
                matchCode = customBarCodes[ScanField.MatchCode.dbField] ?: it.matchCode,
                zoneCheckInId = zoneCheckInId,
                steps = steps,
                scanFlowStep = step
            )

            match != null
        }

        if (options.isEmpty() && this.showDialog) {
            throw MatchFailedException(false)
        }

        return if (options.isEmpty()) null else this.copy(options = options)
    }


    private fun getScanSource(
        step: ScanFlowStepDomainEntity,
        existingScanSource: ScanSource?
        ): ScanSource? {
        return if (step.isBarcodeStep())
            scannerRepository.getScanSource()
        else existingScanSource
    }

    data class Params(
        val zoneCheckInId: String,
        val value: String,
        val allowNotMatched: Boolean,
        val discardMatchCodes: Boolean,
        val scanFlowStepId: String,
        val sendFeedBack: Boolean,
        val clearProperties: Boolean = false
    )
}


//========================================OLD MATCHING LOGIC========================================


private fun ScanFlowStepDomainEntity.applyCustomBarcodes(value: String): Map<String, String> {
    if (customBarcodes == null) return emptyMap()

    return customBarcodes.filter { it.appliesTo(value) }.flatMap { it.barcodeParts ?: emptyList() }
        .mapNotNull { part ->
            val field = part.resultDBField ?: return@mapNotNull null

            val subCode = if (part.startIndex != null && part.length != null) {
                value.substring(part.startIndex).take(part.length)
            } else if (part.startIndex != null) {
                value.substring(part.startIndex)
            } else {
                value
            }

            field to subCode.applyOperations(part.barcodeOperations ?: emptyList())
        }.toMap()
}

private fun CustomBarcodeDomainEntity.appliesTo(value: String): Boolean {
    val matches = regEx?.toRegex()?.containsMatchIn(value) ?: false
    return (regExInverse == true && !matches) || matches
}

private fun String.applyOperations(operations: List<StringOperationDomainEntity>): String {
    return operations.mapNotNull { it.toFunction() }.fold(this) { acc, func ->
        func.execute(acc)
    }
}

private fun ScanFlowStepDomainEntity.validateMatchResult(
    match: MatchDomainEntity?, zoneCode: String?
): MatchValidationResult { // location code warehouse code
    if (valueDBFieldName != ScanField.LocationCode.dbField && match == null) {
        // if it is allowed to proceed with no match and the user should be notified
        // show a message to the user with "cancel" and "ok" options
        if (matchAllowNotMatched == true && matchNotifyNotMatched == true) {
            return MatchValidationResult.NoMatch(true)
        }

        // if it's not allowed to proceed without a match
        // show a message to the user with "ok" option
        if (matchAllowNotMatched == false) {
            return MatchValidationResult.NoMatch(false)
        }
    }

    if (valueDBFieldName == ScanField.LocationCode.dbField && matchResultFieldNames?.contains(
            ScanField.ZoneCode.dbField
        ) == true && zoneCode != null && match?.get(ScanField.ZoneCode.dbField) != zoneCode
    ) {
        return when {
            matchAllowNotMatched == false -> MatchValidationResult.ZoneCodeNotValid(false, zoneCode)
            matchNotifyNotMatched == true -> MatchValidationResult.ZoneCodeNotValid(true, zoneCode)
            else -> MatchValidationResult.Continue(null)
        }
    }

    return MatchValidationResult.Continue(match)
}

private fun ScanFlowStepDomainEntity.getFieldNames(): List<String> {
    val fieldNames = matchResultFieldNames ?: return ScanField.inputs.map { it.name }
    return fieldNames.split(",").map { it.trim() }
        .filter { ScanField.inputs.contains(ScanField.valueOf(it)) }
}

private operator fun MatchDomainEntity.get(field: String): String? {
    return data[field]
}

/**
 * Creates a map that gives the name of the match database for each step
 * that supports matching.
 */
private fun List<ScanFlowStepDomainEntity>.matchDatabasesForSteps(dbName: String): Map<String, String> {
    return mapNotNull { step ->
        if (step.matchType != null && step.valueDBFieldName != null) {
            step.valueDBFieldName to "${dbName}_${step.matchType}_1"
        } else {
            null
        }
    }.toMap()
}

private sealed class MatchValidationResult {
    data class NoMatch(val warning: Boolean) : MatchValidationResult()
    data class ZoneCodeNotValid(val warning: Boolean, val zoneCode: String) :
        MatchValidationResult()

    data class Continue(val match: MatchDomainEntity?) : MatchValidationResult()
}