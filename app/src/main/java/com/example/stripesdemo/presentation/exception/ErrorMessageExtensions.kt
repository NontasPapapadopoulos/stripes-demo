package net.stripesapp.mlsretailsoftware.presentation.exception

import android.content.res.Resources
import android.util.Log
import net.stripesapp.mlsretailsoftware.data.exception.ApiException
import net.stripesapp.mlsretailsoftware.domain.exception.InvalidCountException
import net.stripesapp.mlsretailsoftware.domain.exception.InvalidSecretMenuPasswordException
import net.stripesapp.mlsretailsoftware.domain.exception.InvalidUrlFormatException
import net.stripesapp.mlsretailsoftware.domain.exception.AvailabilityCheckFailed
import net.stripesapp.mlsretailsoftware.domain.exception.CannotSyncOpenZoneCheckIn
import net.stripesapp.mlsretailsoftware.domain.exception.InvalidFileFormatException
import net.stripesapp.mlsretailsoftware.domain.exception.InvalidNumberOfDatabaseFilesException
import net.stripesapp.mlsretailsoftware.domain.exception.InvalidZoneCodeException
import net.stripesapp.mlsretailsoftware.domain.exception.MatchFailedException
import net.stripesapp.mlsretailsoftware.domain.exception.ZoneDoesNotExistException
import net.stripesapp.mlsretailsoftware.presentation.R
import java.net.UnknownHostException

private const val unknownErrorTag: String = "Unknown Error: "

fun Resources.errorStringResource(throwable: Throwable): String {
    Log.e("error", "net", throwable)
    return when (throwable) {
        is AvailabilityCheckFailed -> getString(R.string.exception_availability_check_failed)
        is UnknownHostException -> getString(R.string.exception_message_unknown_host)
        is MatchFailedException -> getString(R.string.no_match_found)
        is InvalidCountException -> getString(R.string.invalid_input)
        is InvalidSecretMenuPasswordException -> getString(R.string.password_incorrect)
        is InvalidUrlFormatException -> getString(R.string.invalid_input)
        is CannotSyncOpenZoneCheckIn -> getString(R.string.cannot_sync_zoneCheckIn)
        is ZoneDoesNotExistException -> getString(R.string.zone_does_not_exist)
        is InvalidZoneCodeException -> getString(R.string.zone_code_is_empty)
        is InvalidFileFormatException -> getString(R.string.wrong_file_format)
        is InvalidNumberOfDatabaseFilesException -> getString(R.string.wrong_number_of_files)
        is ApiException.ErrorResponse -> throwable.errorMessages.joinToString()
        else -> throwable.message ?: (unknownErrorTag + throwable.javaClass.name)
    }
}