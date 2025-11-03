package com.bondy.bondybranch.domain.usecase

import javax.inject.Inject

class SwitchEnvironmentUseCase @Inject constructor() {
    operator fun invoke(targetEnvironment: String) {
        // TODO: update environment persistence once available
    }
}
