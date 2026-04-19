plugins {
	alias(libs.plugins.android.application)
	alias(libs.plugins.kotlin.android)
	alias(libs.plugins.ksp)
	alias(libs.plugins.hilt.android)
}

android {
	namespace = "com.example.samplemvi"
	compileSdk = 35
	
	defaultConfig {
		applicationId = "com.example.samplemvi"
		minSdk = 24
		targetSdk = 35
		versionCode = 1
		versionName = "1.0"
		
		testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
	}
	
	buildFeatures {
		buildConfig = true
		compose = true
	}
	
	buildTypes {
		release {
			isMinifyEnabled = false
			proguardFiles(
				getDefaultProguardFile("proguard-android-optimize.txt"),
				"proguard-rules.pro"
			)
		}
	}
	
	compileOptions {
		sourceCompatibility = JavaVersion.VERSION_17
		targetCompatibility = JavaVersion.VERSION_17
	}
	
	kotlinOptions {
		jvmTarget = "17"
	}
	
	composeOptions {
		kotlinCompilerExtensionVersion = "1.5.10"
	}
}

dependencies {
	implementation(project(":core"))
	implementation(project(":feature:user"))

	// Core Android
	implementation(libs.androidx.core.ktx)
	implementation(libs.androidx.appcompat)
	implementation(libs.material)
	implementation(libs.androidx.constraintlayout)

	// Hilt
	implementation(libs.hilt.android)
	ksp(libs.hilt.compiler)

	// Timber
	implementation(libs.timber)

	// Compose
	implementation(platform(libs.compose.bom))
	implementation(libs.compose.ui)
	implementation(libs.compose.ui.graphics)
	implementation(libs.compose.ui.tooling.preview)
	implementation(libs.compose.material3)
	implementation(libs.activity.compose)
	implementation(libs.androidx.navigation.compose)
	implementation(libs.androidx.hilt.navigation.compose)
	debugImplementation(libs.compose.ui.tooling)
}
