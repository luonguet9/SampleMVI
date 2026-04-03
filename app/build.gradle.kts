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
}

dependencies {
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
}
