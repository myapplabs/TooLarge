# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### Fixed
- Corrected Maven coordinates in README from `llc.applabs.toolarge` to `io.github.myapplabs`
- Added explicit import statements to all code examples in README
- Added troubleshooting section for common setup issues
- Improved installation instructions with repository configuration

## [1.0.0] - 2026-01-02

### Added
- Initial release of TooLarge library
- Core module with platform-agnostic policy engine
- Android module with Activity and Fragment lifecycle hooks
- Bundle size analysis and reporting
- Configurable budget thresholds per origin type
- LogcatSink for logging violations
- CompositeSink for multiple report destinations
- Support for custom ReportSink implementations
- Stacktrace capture for debugging
- Self-test functionality
- Detailed offender reporting with paths and sizes

### Origins Supported
- `ACTIVITY_SAVE_INSTANCE_STATE`
- `ACTIVITY_INTENT_EXTRAS`
- `FRAGMENT_SAVE_INSTANCE_STATE`
- `FRAGMENT_RESTORED_INSTANCE_STATE`
- `FRAGMENT_ARGUMENTS`
- `INTENT_EXTRAS`
- `NAV_BACK_STACK`
- `SAVED_STATE_REGISTRY`

Initial public release.

### Features
- Automatic Bundle size monitoring
- Configurable warning and error thresholds
- Detailed reporting of largest offenders
- Support for Android API 21+
- Zero-overhead when thresholds not exceeded
- JSON serialization support for reports

### Requirements
- Android API 21+ (Lollipop)
- Kotlin 2.0.21+
- AndroidX Fragment 1.8.5+
