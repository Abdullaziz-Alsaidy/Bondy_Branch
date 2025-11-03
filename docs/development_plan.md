# Bondy Branch Development Plan

## Architectural Overview
- MVVM with discrete `data`, `domain`, and `presentation` layers.
- Hilt for dependency injection, Coroutines + Flow for async work, Retrofit abstraction backed by a fake remote source for now, Room (or in-memory) for local persistence later.
- Compose Navigation drives navigation; Material 3 for UI.

## Phase 1 – Data Foundations (Current focus)
1. **Domain models & support types**
   - Serializable models for `LoyaltyCard`, `Transaction`, `Brand`, `Branch`, `BranchDailyStats`, `AuthSession`.
   - Supporting enums for transaction metadata and a reusable `NetworkResult` sealed class.
2. **Remote contracts**
   - Retrofit-like service interface (`BondyApiService`) describing endpoints.
   - Fake remote data source implementing loyalty logic (sale/redeem, stats, token issuance).
3. **Repository**
   - `BondyRepository` interface (domain façade).
   - `BondyRepositoryImpl` orchestrating remote calls and exposing `Flow<NetworkResult<…>>`.
4. **DI**
   - Hilt module binding the repository implementation and exposing the fake remote source.

## Phase 2 – Domain Layer
1. Implement use cases (`LoginUseCase`, `FetchCardUseCase`, `ProcessSaleUseCase`, `ProcessRedeemUseCase`, `ObserveBranchStatsUseCase`, `ObserveTransactionsUseCase`, `SwitchEnvironmentUseCase`, `LogoutUseCase`).
2. Add supporting utilities (validators, formatting helpers).

## Phase 3 – Presentation Layer
1. **Navigation & Root**
   - Configure navigation graph (Login → Dashboard → Scan → Card Details → History → Settings) and bottom navigation.
2. **Screens + State**
   - Compose UI with ViewModels per screen covering login, dashboard, scan/manual entry, card details, history, settings.
3. **Shared UI**
   - Shared components (progress indicators, loyalty card progress meter, transaction list item templates).
4. **Local persistence**
   - Room setup for transaction history & cached card data.

## Folder Roadmap
- `app/src/main/java/com/bondy/bondybranch/core/`
  - `network/NetworkResult.kt`
  - `serialization/DateSerializer.kt`
- `app/src/main/java/com/bondy/bondybranch/data/`
  - `model/` (`LoyaltyCard.kt`, `Transaction.kt`, `Brand.kt`, `Branch.kt`, `BranchDailyStats.kt`, `AuthSession.kt`, `TransactionEnums.kt`)
  - `remote/api/BondyApiService.kt`
  - `remote/source/FakeRemoteDataSource.kt`
  - `repository/BondyRepositoryImpl.kt`
- `app/src/main/java/com/bondy/bondybranch/domain/`
  - `repository/BondyRepository.kt`
  - `usecase/…` (Phase 2)
- `app/src/main/java/com/bondy/bondybranch/di/`
  - `DataModule.kt`
- `app/src/main/java/com/bondy/bondybranch/presentation/`
  - `navigation/`, `screens/`, `components/`, `viewmodel/`
- `app/src/main/java/com/bondy/bondybranch/local/`
  - Room entities, DAO, database (Phase 3)

Next up: complete Phase 1 Step 1 implementation (models, fake remote, repository, DI).
