# SampleMVI Architecture Guidelines

This file serves as the definitive guide for AI assistants maintaining and evolving this codebase.
DO NOT deviate from these predefined patterns.

## 1. Core Architecture (MVI)
This project strictly follows **Clean Architecture** combined with the **MVI (Model-View-Intent)** presentation pattern.

### The MVI Triad
- **Intent**: User actions or lifecycle events (e.g., `LoadData`, `SubmitButton`).
- **State**: The immutable data holding the UI snapshot (e.g., `isLoading`, `data`, `error`).
- **Effect**: One-off events that shouldn't be persisted in state (e.g., `ShowToast`, `NavigateToMain`).

### Rules for ViewModels:
1. ALL ViewModels MUST inherit from `BaseViewModel<Intent, State, Effect>`.
2. Do NOT use `MutableStateFlow` or `LiveData` directly inside the ViewModel to expose UI state, as `BaseViewModel` already handles it via `updateState {}`.
3. ViewModels MUST NOT contain any Android Framework references (e.g., `Context`, `Activity`, `View`).
4. Intent handling MUST go through `override fun handleIntent(intent: I)`. 

### Rules for UseCases:
1. All UseCases MUST inherit from `UseCase<P, R>`, `NoParamsUseCase<R>`, `FlowUseCase<P, R>`, or `NoParamsFlowUseCase<R>`.
2. Execution logic must be placed in `protected suspend fun execute(...)`.
3. Callers (ViewModels) must invoke UseCases using the operator overload `useCase(params)`.
4. **Business Logic Core**: UseCases are the EXCLUSIVE layer for Business Logic. They must handle all domain rules, combine multiple Repositories if necessary, orchestrate complex data flows, and ensure validation before hitting the repository. Do NOT leak business logic into the ViewModel.

### Rules for Compose UI:
1. Always hoist state. Collect state using `viewModel.state.collectAsState()`.
2. Collect effects inside a `LaunchedEffect` block.
3. Fire events to the ViewModel exclusively via `viewModel.processIntent(Intent(...))`. Do NOT call ViewModel methods directly except `processIntent`.

## 2. Data Layer & Mappers
- **Strict Data Segregation**: API responses (`*Dto.kt`), Database entries (`*Entity.kt`), and UI models (`*Model.kt` or `User.kt`) MUST be strictly separated.
- **Domain Models**: Classes in `domain/model` must be pure Kotlin data classes. Absolutely NO `@Entity`, `@SerializedName`, or Android annotations here.
- **Mappers**: ALL conversions between DTOs, Entities, and Domain models MUST use `com.example.core.base.mapper.Mapper<I, O>`. Do not write arbitrary extension functions for mapping unless it implements this interface.
- **Data Sources**: The Repository implementation (`*RepositoryImpl`) must NOT perform raw API or DB calls. It must delegate to injected Data Sources (Local/Remote).
- **DomainResult**: All Repositories and UseCases MUST return `com.example.core.domain.result.DomainResult<T>`. NEVER use Kotlin's built-in `Result<T>` in the Domain layer, as it bypasses the `DomainException` bounds.

## 3. Dependency Injection (Hilt)
- Always use constructor injection `@Inject constructor(...)`.
- `Repository` bindings (binding Interface to Impl) must be provided in their respective module's `di` package (e.g. `:feature:user/.../di/RepositoryModule.kt`), except for globally shared repos which go in `:core/di`.

## 4. Project Modularization & Navigation
The project is strictly separated into Modules:
1. `:app`: Only contains `MainActivity` and wiring (`NavHost`).
2. `:core`: Contains DI setups, Base Classes, remote API config, global utilities, Database instances.
3. `:feature:*`: Independent features (e.g., `:feature:user`, `:feature:auth`, `:feature:chat`). 
    - A feature shouldn't import another feature directly (except via routing graphs in `app`).
    - Every feature MUST expose a `NavGraphBuilder.featureGraph(...)` extension in its `presentation` layer for the `:app` module to consume.

## 5. Error Handling and Logging
- Do NOT use `println()`, `e.printStackTrace()`, or raw `Timber`.
- Use the **`AppLogger`** utility (`AppLogger.d()`, `AppLogger.logNetwork()`, `AppLogger.logUseCase()`) for all logging. BaseViewModel automatically logs all intents, states, and effects.
