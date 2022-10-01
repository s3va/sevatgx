set(TGVOIP_DIR "${THIRDPARTY_DIR}/libtgvoip")

# TODO move to "${THIRDPARTY_DIR}/libtgvoip/CMakeLists.txt"
add_library(tgvoip STATIC
  "${TGVOIP_DIR}/logging.cpp"
  "${TGVOIP_DIR}/VoIPController.cpp"
  "${TGVOIP_DIR}/VoIPGroupController.cpp"
  "${TGVOIP_DIR}/Buffers.cpp"
  "${TGVOIP_DIR}/BlockingQueue.cpp"
  "${TGVOIP_DIR}/audio/AudioInput.cpp"
  "${TGVOIP_DIR}/os/android/AudioInputOpenSLES.cpp"
  "${TGVOIP_DIR}/MediaStreamItf.cpp"
  "${TGVOIP_DIR}/audio/AudioOutput.cpp"
  "${TGVOIP_DIR}/OpusEncoder.cpp"
  "${TGVOIP_DIR}/os/android/AudioOutputOpenSLES.cpp"
  "${TGVOIP_DIR}/JitterBuffer.cpp"
  "${TGVOIP_DIR}/OpusDecoder.cpp"
  "${TGVOIP_DIR}/os/android/OpenSLEngineWrapper.cpp"
  "${TGVOIP_DIR}/os/android/AudioInputAndroid.cpp"
  "${TGVOIP_DIR}/os/android/AudioOutputAndroid.cpp"
  "${TGVOIP_DIR}/EchoCanceller.cpp"
  "${TGVOIP_DIR}/CongestionControl.cpp"
  "${TGVOIP_DIR}/VoIPServerConfig.cpp"
  "${TGVOIP_DIR}/audio/Resampler.cpp"
  "${TGVOIP_DIR}/NetworkSocket.cpp"
  "${TGVOIP_DIR}/os/posix/NetworkSocketPosix.cpp"
  "${TGVOIP_DIR}/PacketReassembler.cpp"
  "${TGVOIP_DIR}/MessageThread.cpp"
  "${TGVOIP_DIR}/json11.cpp"
  "${TGVOIP_DIR}/audio/AudioIO.cpp"
  "${TGVOIP_DIR}/video/VideoRenderer.cpp"
  "${TGVOIP_DIR}/video/VideoSource.cpp"
  "${TGVOIP_DIR}/video/ScreamCongestionController.cpp"
  "${TGVOIP_DIR}/os/android/VideoSourceAndroid.cpp"
  "${TGVOIP_DIR}/os/android/VideoRendererAndroid.cpp"
  "${TGVOIP_DIR}/client/android/tg_voip_jni.cpp"
)
if (${ANDROID_ABI} STREQUAL "armeabi-v7a")
  target_compile_definitions(tgvoip PRIVATE
    WEBRTC_HAS_NEON
  )
elseif(${ANDROID_ABI} STREQUAL "arm64-v8a")
  target_compile_definitions(tgvoip PRIVATE
    WEBRTC_HAS_NEON
    __ARM64_NEON__
  )
endif()
set(CC_NEON "cc")
target_sources(tgvoip PRIVATE
  "${TGVOIP_DIR}/webrtc_dsp/system_wrappers/source/field_trial.cc"
  "${TGVOIP_DIR}/webrtc_dsp/system_wrappers/source/metrics.cc"
  "${TGVOIP_DIR}/webrtc_dsp/system_wrappers/source/cpu_features.cc"
  "${TGVOIP_DIR}/webrtc_dsp/absl/strings/internal/memutil.cc"
  "${TGVOIP_DIR}/webrtc_dsp/absl/strings/string_view.cc"
  "${TGVOIP_DIR}/webrtc_dsp/absl/strings/ascii.cc"
  "${TGVOIP_DIR}/webrtc_dsp/absl/types/bad_optional_access.cc"
  "${TGVOIP_DIR}/webrtc_dsp/absl/types/optional.cc"
  "${TGVOIP_DIR}/webrtc_dsp/absl/base/internal/raw_logging.cc"
  "${TGVOIP_DIR}/webrtc_dsp/absl/base/internal/throw_delegate.cc"
  "${TGVOIP_DIR}/webrtc_dsp/rtc_base/race_checker.cc"
  "${TGVOIP_DIR}/webrtc_dsp/rtc_base/strings/string_builder.cc"
  "${TGVOIP_DIR}/webrtc_dsp/rtc_base/memory/aligned_malloc.cc"
  "${TGVOIP_DIR}/webrtc_dsp/rtc_base/timeutils.cc"
  "${TGVOIP_DIR}/webrtc_dsp/rtc_base/platform_file.cc"
  "${TGVOIP_DIR}/webrtc_dsp/rtc_base/string_to_number.cc"
  "${TGVOIP_DIR}/webrtc_dsp/rtc_base/thread_checker_impl.cc"
  "${TGVOIP_DIR}/webrtc_dsp/rtc_base/stringencode.cc"
  "${TGVOIP_DIR}/webrtc_dsp/rtc_base/stringutils.cc"
  "${TGVOIP_DIR}/webrtc_dsp/rtc_base/checks.cc"
  "${TGVOIP_DIR}/webrtc_dsp/rtc_base/platform_thread.cc"
  "${TGVOIP_DIR}/webrtc_dsp/rtc_base/criticalsection.cc"
  "${TGVOIP_DIR}/webrtc_dsp/rtc_base/platform_thread_types.cc"
  "${TGVOIP_DIR}/webrtc_dsp/rtc_base/event.cc"
  "${TGVOIP_DIR}/webrtc_dsp/rtc_base/event_tracer.cc"
  "${TGVOIP_DIR}/webrtc_dsp/rtc_base/logging_webrtc.cc"
  "${TGVOIP_DIR}/webrtc_dsp/third_party/rnnoise/src/rnn_vad_weights.cc"
  "${TGVOIP_DIR}/webrtc_dsp/third_party/rnnoise/src/kiss_fft.cc"
  "${TGVOIP_DIR}/webrtc_dsp/api/audio/audio_frame.cc"
  "${TGVOIP_DIR}/webrtc_dsp/api/audio/echo_canceller3_config.cc"
  "${TGVOIP_DIR}/webrtc_dsp/api/audio/echo_canceller3_factory.cc"
  "${TGVOIP_DIR}/webrtc_dsp/modules/third_party/fft/fft.c"
  "${TGVOIP_DIR}/webrtc_dsp/modules/audio_coding/codecs/isac/main/source/pitch_estimator.c"
  "${TGVOIP_DIR}/webrtc_dsp/modules/audio_coding/codecs/isac/main/source/lpc_shape_swb16_tables.c"
  "${TGVOIP_DIR}/webrtc_dsp/modules/audio_coding/codecs/isac/main/source/pitch_gain_tables.c"
  "${TGVOIP_DIR}/webrtc_dsp/modules/audio_coding/codecs/isac/main/source/arith_routines_logist.c"
  "${TGVOIP_DIR}/webrtc_dsp/modules/audio_coding/codecs/isac/main/source/filterbanks.c"
  "${TGVOIP_DIR}/webrtc_dsp/modules/audio_coding/codecs/isac/main/source/transform.c"
  "${TGVOIP_DIR}/webrtc_dsp/modules/audio_coding/codecs/isac/main/source/pitch_filter.c"
  "${TGVOIP_DIR}/webrtc_dsp/modules/audio_coding/codecs/isac/main/source/encode_lpc_swb.c"
  "${TGVOIP_DIR}/webrtc_dsp/modules/audio_coding/codecs/isac/main/source/filter_functions.c"
  "${TGVOIP_DIR}/webrtc_dsp/modules/audio_coding/codecs/isac/main/source/decode.c"
  "${TGVOIP_DIR}/webrtc_dsp/modules/audio_coding/codecs/isac/main/source/lattice.c"
  "${TGVOIP_DIR}/webrtc_dsp/modules/audio_coding/codecs/isac/main/source/intialize.c"
  "${TGVOIP_DIR}/webrtc_dsp/modules/audio_coding/codecs/isac/main/source/lpc_tables.c"
  "${TGVOIP_DIR}/webrtc_dsp/modules/audio_coding/codecs/isac/main/source/lpc_gain_swb_tables.c"
  "${TGVOIP_DIR}/webrtc_dsp/modules/audio_coding/codecs/isac/main/source/bandwidth_estimator.c"
  "${TGVOIP_DIR}/webrtc_dsp/modules/audio_coding/codecs/isac/main/source/encode.c"
  "${TGVOIP_DIR}/webrtc_dsp/modules/audio_coding/codecs/isac/main/source/lpc_analysis.c"
  "${TGVOIP_DIR}/webrtc_dsp/modules/audio_coding/codecs/isac/main/source/arith_routines_hist.c"
  "${TGVOIP_DIR}/webrtc_dsp/modules/audio_coding/codecs/isac/main/source/entropy_coding.c"
  "${TGVOIP_DIR}/webrtc_dsp/modules/audio_coding/codecs/isac/main/source/isac_vad.c"
  "${TGVOIP_DIR}/webrtc_dsp/modules/audio_coding/codecs/isac/main/source/arith_routines.c"
  "${TGVOIP_DIR}/webrtc_dsp/modules/audio_coding/codecs/isac/main/source/crc.c"
  "${TGVOIP_DIR}/webrtc_dsp/modules/audio_coding/codecs/isac/main/source/lpc_shape_swb12_tables.c"
  "${TGVOIP_DIR}/webrtc_dsp/modules/audio_coding/codecs/isac/main/source/decode_bwe.c"
  "${TGVOIP_DIR}/webrtc_dsp/modules/audio_coding/codecs/isac/main/source/spectrum_ar_model_tables.c"
  "${TGVOIP_DIR}/webrtc_dsp/modules/audio_coding/codecs/isac/main/source/pitch_lag_tables.c"
  "${TGVOIP_DIR}/webrtc_dsp/modules/audio_coding/codecs/isac/main/source/isac.c"
  "${TGVOIP_DIR}/webrtc_dsp/modules/audio_processing/rms_level.cc"
  "${TGVOIP_DIR}/webrtc_dsp/modules/audio_processing/echo_detector/normalized_covariance_estimator.cc"
  "${TGVOIP_DIR}/webrtc_dsp/modules/audio_processing/echo_detector/moving_max.cc"
  "${TGVOIP_DIR}/webrtc_dsp/modules/audio_processing/echo_detector/circular_buffer.cc"
  "${TGVOIP_DIR}/webrtc_dsp/modules/audio_processing/echo_detector/mean_variance_estimator.cc"
  "${TGVOIP_DIR}/webrtc_dsp/modules/audio_processing/splitting_filter.cc"
  "${TGVOIP_DIR}/webrtc_dsp/modules/audio_processing/gain_control_impl.cc"
  "${TGVOIP_DIR}/webrtc_dsp/modules/audio_processing/ns/nsx_core.c"
  "${TGVOIP_DIR}/webrtc_dsp/modules/audio_processing/ns/noise_suppression_x.c"
  "${TGVOIP_DIR}/webrtc_dsp/modules/audio_processing/ns/nsx_core_c.c"
  "${TGVOIP_DIR}/webrtc_dsp/modules/audio_processing/ns/ns_core.c"
  "${TGVOIP_DIR}/webrtc_dsp/modules/audio_processing/ns/noise_suppression.c"
  "${TGVOIP_DIR}/webrtc_dsp/modules/audio_processing/audio_buffer.cc"
  "${TGVOIP_DIR}/webrtc_dsp/modules/audio_processing/typing_detection.cc"
  "${TGVOIP_DIR}/webrtc_dsp/modules/audio_processing/include/audio_processing_statistics.cc"
  "${TGVOIP_DIR}/webrtc_dsp/modules/audio_processing/include/audio_generator_factory.cc"
  "${TGVOIP_DIR}/webrtc_dsp/modules/audio_processing/include/aec_dump.cc"
  "${TGVOIP_DIR}/webrtc_dsp/modules/audio_processing/include/audio_processing.cc"
  "${TGVOIP_DIR}/webrtc_dsp/modules/audio_processing/include/config.cc"
  "${TGVOIP_DIR}/webrtc_dsp/modules/audio_processing/agc2/interpolated_gain_curve.cc"
  "${TGVOIP_DIR}/webrtc_dsp/modules/audio_processing/agc2/agc2_common.cc"
  "${TGVOIP_DIR}/webrtc_dsp/modules/audio_processing/agc2/gain_applier.cc"
  "${TGVOIP_DIR}/webrtc_dsp/modules/audio_processing/agc2/adaptive_agc.cc"
  "${TGVOIP_DIR}/webrtc_dsp/modules/audio_processing/agc2/adaptive_digital_gain_applier.cc"
  "${TGVOIP_DIR}/webrtc_dsp/modules/audio_processing/agc2/limiter.cc"
  "${TGVOIP_DIR}/webrtc_dsp/modules/audio_processing/agc2/saturation_protector.cc"
  "${TGVOIP_DIR}/webrtc_dsp/modules/audio_processing/agc2/rnn_vad/spectral_features_internal.cc"
  "${TGVOIP_DIR}/webrtc_dsp/modules/audio_processing/agc2/rnn_vad/rnn.cc"
  "${TGVOIP_DIR}/webrtc_dsp/modules/audio_processing/agc2/rnn_vad/pitch_search_internal.cc"
  "${TGVOIP_DIR}/webrtc_dsp/modules/audio_processing/agc2/rnn_vad/spectral_features.cc"
  "${TGVOIP_DIR}/webrtc_dsp/modules/audio_processing/agc2/rnn_vad/pitch_search.cc"
  "${TGVOIP_DIR}/webrtc_dsp/modules/audio_processing/agc2/rnn_vad/features_extraction.cc"
  "${TGVOIP_DIR}/webrtc_dsp/modules/audio_processing/agc2/rnn_vad/fft_util.cc"
  "${TGVOIP_DIR}/webrtc_dsp/modules/audio_processing/agc2/rnn_vad/lp_residual.cc"
  "${TGVOIP_DIR}/webrtc_dsp/modules/audio_processing/agc2/adaptive_mode_level_estimator_agc.cc"
  "${TGVOIP_DIR}/webrtc_dsp/modules/audio_processing/agc2/vector_float_frame.cc"
  "${TGVOIP_DIR}/webrtc_dsp/modules/audio_processing/agc2/noise_level_estimator.cc"
  "${TGVOIP_DIR}/webrtc_dsp/modules/audio_processing/agc2/agc2_testing_common.cc"
  "${TGVOIP_DIR}/webrtc_dsp/modules/audio_processing/agc2/fixed_digital_level_estimator.cc"
  "${TGVOIP_DIR}/webrtc_dsp/modules/audio_processing/agc2/fixed_gain_controller.cc"
  "${TGVOIP_DIR}/webrtc_dsp/modules/audio_processing/agc2/vad_with_level.cc"
  "${TGVOIP_DIR}/webrtc_dsp/modules/audio_processing/agc2/limiter_db_gain_curve.cc"
  "${TGVOIP_DIR}/webrtc_dsp/modules/audio_processing/agc2/down_sampler.cc"
  "${TGVOIP_DIR}/webrtc_dsp/modules/audio_processing/agc2/signal_classifier.cc"
  "${TGVOIP_DIR}/webrtc_dsp/modules/audio_processing/agc2/noise_spectrum_estimator.cc"
  "${TGVOIP_DIR}/webrtc_dsp/modules/audio_processing/agc2/compute_interpolated_gain_curve.cc"
  "${TGVOIP_DIR}/webrtc_dsp/modules/audio_processing/agc2/biquad_filter.cc"
  "${TGVOIP_DIR}/webrtc_dsp/modules/audio_processing/agc2/adaptive_mode_level_estimator.cc"
  "${TGVOIP_DIR}/webrtc_dsp/modules/audio_processing/transient/moving_moments.cc"
  "${TGVOIP_DIR}/webrtc_dsp/modules/audio_processing/transient/wpd_tree.cc"
  "${TGVOIP_DIR}/webrtc_dsp/modules/audio_processing/transient/wpd_node.cc"
  "${TGVOIP_DIR}/webrtc_dsp/modules/audio_processing/transient/transient_suppressor.cc"
  "${TGVOIP_DIR}/webrtc_dsp/modules/audio_processing/transient/transient_detector.cc"
  "${TGVOIP_DIR}/webrtc_dsp/modules/audio_processing/low_cut_filter.cc"
  "${TGVOIP_DIR}/webrtc_dsp/modules/audio_processing/level_estimator_impl.cc"
  "${TGVOIP_DIR}/webrtc_dsp/modules/audio_processing/three_band_filter_bank.cc"
  "${TGVOIP_DIR}/webrtc_dsp/modules/audio_processing/aec/echo_cancellation.cc"
  "${TGVOIP_DIR}/webrtc_dsp/modules/audio_processing/aec/aec_resampler.cc"
  "${TGVOIP_DIR}/webrtc_dsp/modules/audio_processing/aec/aec_core.cc"
  "${TGVOIP_DIR}/webrtc_dsp/modules/audio_processing/voice_detection_impl.cc"
  "${TGVOIP_DIR}/webrtc_dsp/modules/audio_processing/echo_cancellation_impl.cc"
  "${TGVOIP_DIR}/webrtc_dsp/modules/audio_processing/gain_control_for_experimental_agc.cc"
  "${TGVOIP_DIR}/webrtc_dsp/modules/audio_processing/agc/agc.cc"
  "${TGVOIP_DIR}/webrtc_dsp/modules/audio_processing/agc/loudness_histogram.cc"
  "${TGVOIP_DIR}/webrtc_dsp/modules/audio_processing/agc/agc_manager_direct.cc"
  "${TGVOIP_DIR}/webrtc_dsp/modules/audio_processing/agc/legacy/analog_agc.c"
  "${TGVOIP_DIR}/webrtc_dsp/modules/audio_processing/agc/legacy/digital_agc.c"
  "${TGVOIP_DIR}/webrtc_dsp/modules/audio_processing/agc/utility.cc"
  "${TGVOIP_DIR}/webrtc_dsp/modules/audio_processing/audio_processing_impl.cc"
  "${TGVOIP_DIR}/webrtc_dsp/modules/audio_processing/audio_generator/file_audio_generator.cc"
  "${TGVOIP_DIR}/webrtc_dsp/modules/audio_processing/gain_controller2.cc"
  "${TGVOIP_DIR}/webrtc_dsp/modules/audio_processing/residual_echo_detector.cc"
  "${TGVOIP_DIR}/webrtc_dsp/modules/audio_processing/noise_suppression_impl.cc"
  "${TGVOIP_DIR}/webrtc_dsp/modules/audio_processing/aecm/aecm_core.cc"
  "${TGVOIP_DIR}/webrtc_dsp/modules/audio_processing/aecm/aecm_core_c.cc"
  "${TGVOIP_DIR}/webrtc_dsp/modules/audio_processing/aecm/echo_control_mobile.cc"
  "${TGVOIP_DIR}/webrtc_dsp/modules/audio_processing/aec3/render_reverb_model.cc"
  "${TGVOIP_DIR}/webrtc_dsp/modules/audio_processing/aec3/reverb_model_fallback.cc"
  "${TGVOIP_DIR}/webrtc_dsp/modules/audio_processing/aec3/echo_remover_metrics.cc"
  "${TGVOIP_DIR}/webrtc_dsp/modules/audio_processing/aec3/matched_filter_lag_aggregator.cc"
  "${TGVOIP_DIR}/webrtc_dsp/modules/audio_processing/aec3/render_delay_buffer2.cc"
  "${TGVOIP_DIR}/webrtc_dsp/modules/audio_processing/aec3/echo_path_variability.cc"
  "${TGVOIP_DIR}/webrtc_dsp/modules/audio_processing/aec3/frame_blocker.cc"
  "${TGVOIP_DIR}/webrtc_dsp/modules/audio_processing/aec3/subtractor.cc"
  "${TGVOIP_DIR}/webrtc_dsp/modules/audio_processing/aec3/aec3_fft.cc"
  "${TGVOIP_DIR}/webrtc_dsp/modules/audio_processing/aec3/fullband_erle_estimator.cc"
  "${TGVOIP_DIR}/webrtc_dsp/modules/audio_processing/aec3/suppression_filter.${CC_NEON}"
  "${TGVOIP_DIR}/webrtc_dsp/modules/audio_processing/aec3/block_processor.cc"
  "${TGVOIP_DIR}/webrtc_dsp/modules/audio_processing/aec3/subband_erle_estimator.cc"
  "${TGVOIP_DIR}/webrtc_dsp/modules/audio_processing/aec3/render_delay_controller_metrics.cc"
  "${TGVOIP_DIR}/webrtc_dsp/modules/audio_processing/aec3/render_delay_buffer.cc"
  "${TGVOIP_DIR}/webrtc_dsp/modules/audio_processing/aec3/vector_buffer.cc"
  "${TGVOIP_DIR}/webrtc_dsp/modules/audio_processing/aec3/erl_estimator.cc"
  "${TGVOIP_DIR}/webrtc_dsp/modules/audio_processing/aec3/aec_state.cc"
  "${TGVOIP_DIR}/webrtc_dsp/modules/audio_processing/aec3/adaptive_fir_filter.${CC_NEON}"
  "${TGVOIP_DIR}/webrtc_dsp/modules/audio_processing/aec3/render_delay_controller.cc"
  "${TGVOIP_DIR}/webrtc_dsp/modules/audio_processing/aec3/skew_estimator.cc"
  "${TGVOIP_DIR}/webrtc_dsp/modules/audio_processing/aec3/echo_path_delay_estimator.cc"
  "${TGVOIP_DIR}/webrtc_dsp/modules/audio_processing/aec3/block_framer.cc"
  "${TGVOIP_DIR}/webrtc_dsp/modules/audio_processing/aec3/erle_estimator.cc"
  "${TGVOIP_DIR}/webrtc_dsp/modules/audio_processing/aec3/reverb_model.cc"
  "${TGVOIP_DIR}/webrtc_dsp/modules/audio_processing/aec3/cascaded_biquad_filter.cc"
  "${TGVOIP_DIR}/webrtc_dsp/modules/audio_processing/aec3/render_buffer.cc"
  "${TGVOIP_DIR}/webrtc_dsp/modules/audio_processing/aec3/subtractor_output.cc"
  "${TGVOIP_DIR}/webrtc_dsp/modules/audio_processing/aec3/stationarity_estimator.cc"
  "${TGVOIP_DIR}/webrtc_dsp/modules/audio_processing/aec3/render_signal_analyzer.cc"
  "${TGVOIP_DIR}/webrtc_dsp/modules/audio_processing/aec3/subtractor_output_analyzer.cc"
  "${TGVOIP_DIR}/webrtc_dsp/modules/audio_processing/aec3/suppression_gain.${CC_NEON}"
  "${TGVOIP_DIR}/webrtc_dsp/modules/audio_processing/aec3/echo_audibility.cc"
  "${TGVOIP_DIR}/webrtc_dsp/modules/audio_processing/aec3/block_processor_metrics.cc"
  "${TGVOIP_DIR}/webrtc_dsp/modules/audio_processing/aec3/moving_average.cc"
  "${TGVOIP_DIR}/webrtc_dsp/modules/audio_processing/aec3/reverb_model_estimator.cc"
  "${TGVOIP_DIR}/webrtc_dsp/modules/audio_processing/aec3/aec3_common.cc"
  "${TGVOIP_DIR}/webrtc_dsp/modules/audio_processing/aec3/residual_echo_estimator.cc"
  "${TGVOIP_DIR}/webrtc_dsp/modules/audio_processing/aec3/matched_filter.${CC_NEON}"
  "${TGVOIP_DIR}/webrtc_dsp/modules/audio_processing/aec3/reverb_decay_estimator.cc"
  "${TGVOIP_DIR}/webrtc_dsp/modules/audio_processing/aec3/render_delay_controller2.cc"
  "${TGVOIP_DIR}/webrtc_dsp/modules/audio_processing/aec3/suppression_gain_limiter.cc"
  "${TGVOIP_DIR}/webrtc_dsp/modules/audio_processing/aec3/main_filter_update_gain.cc"
  "${TGVOIP_DIR}/webrtc_dsp/modules/audio_processing/aec3/echo_remover.cc"
  "${TGVOIP_DIR}/webrtc_dsp/modules/audio_processing/aec3/downsampled_render_buffer.cc"
  "${TGVOIP_DIR}/webrtc_dsp/modules/audio_processing/aec3/matrix_buffer.cc"
  "${TGVOIP_DIR}/webrtc_dsp/modules/audio_processing/aec3/block_processor2.cc"
  "${TGVOIP_DIR}/webrtc_dsp/modules/audio_processing/aec3/echo_canceller3.cc"
  "${TGVOIP_DIR}/webrtc_dsp/modules/audio_processing/aec3/block_delay_buffer.cc"
  "${TGVOIP_DIR}/webrtc_dsp/modules/audio_processing/aec3/fft_buffer.cc"
  "${TGVOIP_DIR}/webrtc_dsp/modules/audio_processing/aec3/comfort_noise_generator.${CC_NEON}"
  "${TGVOIP_DIR}/webrtc_dsp/modules/audio_processing/aec3/shadow_filter_update_gain.cc"
  "${TGVOIP_DIR}/webrtc_dsp/modules/audio_processing/aec3/filter_analyzer.cc"
  "${TGVOIP_DIR}/webrtc_dsp/modules/audio_processing/aec3/reverb_frequency_response.cc"
  "${TGVOIP_DIR}/webrtc_dsp/modules/audio_processing/aec3/decimator.cc"
  "${TGVOIP_DIR}/webrtc_dsp/modules/audio_processing/echo_control_mobile_impl.cc"
  "${TGVOIP_DIR}/webrtc_dsp/modules/audio_processing/logging/apm_data_dumper.cc"
  "${TGVOIP_DIR}/webrtc_dsp/modules/audio_processing/vad/voice_activity_detector.cc"
  "${TGVOIP_DIR}/webrtc_dsp/modules/audio_processing/vad/standalone_vad.cc"
  "${TGVOIP_DIR}/webrtc_dsp/modules/audio_processing/vad/pitch_internal.cc"
  "${TGVOIP_DIR}/webrtc_dsp/modules/audio_processing/vad/vad_circular_buffer.cc"
  "${TGVOIP_DIR}/webrtc_dsp/modules/audio_processing/vad/vad_audio_proc.cc"
  "${TGVOIP_DIR}/webrtc_dsp/modules/audio_processing/vad/pole_zero_filter.cc"
  "${TGVOIP_DIR}/webrtc_dsp/modules/audio_processing/vad/pitch_based_vad.cc"
  "${TGVOIP_DIR}/webrtc_dsp/modules/audio_processing/vad/gmm.cc"
  "${TGVOIP_DIR}/webrtc_dsp/modules/audio_processing/utility/ooura_fft.cc"
  "${TGVOIP_DIR}/webrtc_dsp/modules/audio_processing/utility/delay_estimator_wrapper.cc"
  "${TGVOIP_DIR}/webrtc_dsp/modules/audio_processing/utility/delay_estimator.cc"
  "${TGVOIP_DIR}/webrtc_dsp/modules/audio_processing/utility/block_mean_calculator.cc"
  "${TGVOIP_DIR}/webrtc_dsp/common_audio/window_generator.cc"
  "${TGVOIP_DIR}/webrtc_dsp/common_audio/channel_buffer.cc"
  "${TGVOIP_DIR}/webrtc_dsp/common_audio/fir_filter_factory.cc"
  "${TGVOIP_DIR}/webrtc_dsp/common_audio/wav_header.cc"
  "${TGVOIP_DIR}/webrtc_dsp/common_audio/real_fourier_ooura.cc"
  "${TGVOIP_DIR}/webrtc_dsp/common_audio/audio_util.cc"
  "${TGVOIP_DIR}/webrtc_dsp/common_audio/resampler/push_sinc_resampler.cc"
  "${TGVOIP_DIR}/webrtc_dsp/common_audio/resampler/resampler.cc"
  "${TGVOIP_DIR}/webrtc_dsp/common_audio/resampler/push_resampler.cc"
  "${TGVOIP_DIR}/webrtc_dsp/common_audio/resampler/sinc_resampler.cc"
  "${TGVOIP_DIR}/webrtc_dsp/common_audio/resampler/sinusoidal_linear_chirp_source.cc"
  "${TGVOIP_DIR}/webrtc_dsp/common_audio/wav_file.cc"
  "${TGVOIP_DIR}/webrtc_dsp/common_audio/third_party/spl_sqrt_floor/spl_sqrt_floor.c"
  "${TGVOIP_DIR}/webrtc_dsp/common_audio/third_party/fft4g/fft4g.c"
  "${TGVOIP_DIR}/webrtc_dsp/common_audio/audio_converter.cc"
  "${TGVOIP_DIR}/webrtc_dsp/common_audio/real_fourier.cc"
  "${TGVOIP_DIR}/webrtc_dsp/common_audio/sparse_fir_filter.cc"
  "${TGVOIP_DIR}/webrtc_dsp/common_audio/smoothing_filter.cc"
  "${TGVOIP_DIR}/webrtc_dsp/common_audio/fir_filter_c.cc"
  "${TGVOIP_DIR}/webrtc_dsp/common_audio/ring_buffer.c"
  "${TGVOIP_DIR}/webrtc_dsp/common_audio/signal_processing/complex_fft.c"
  "${TGVOIP_DIR}/webrtc_dsp/common_audio/signal_processing/filter_ma_fast_q12.c"
  "${TGVOIP_DIR}/webrtc_dsp/common_audio/signal_processing/levinson_durbin.c"
  "${TGVOIP_DIR}/webrtc_dsp/common_audio/signal_processing/dot_product_with_scale.cc"
  "${TGVOIP_DIR}/webrtc_dsp/common_audio/signal_processing/auto_corr_to_refl_coef.c"
  "${TGVOIP_DIR}/webrtc_dsp/common_audio/signal_processing/resample_by_2_internal.c"
  "${TGVOIP_DIR}/webrtc_dsp/common_audio/signal_processing/energy.c"
  "${TGVOIP_DIR}/webrtc_dsp/common_audio/signal_processing/sqrt_of_one_minus_x_squared.c"
  "${TGVOIP_DIR}/webrtc_dsp/common_audio/signal_processing/downsample_fast.c"
  "${TGVOIP_DIR}/webrtc_dsp/common_audio/signal_processing/splitting_filter1.c"
  "${TGVOIP_DIR}/webrtc_dsp/common_audio/signal_processing/filter_ar_fast_q12.c"
  "${TGVOIP_DIR}/webrtc_dsp/common_audio/signal_processing/spl_init.c"
  "${TGVOIP_DIR}/webrtc_dsp/common_audio/signal_processing/lpc_to_refl_coef.c"
  "${TGVOIP_DIR}/webrtc_dsp/common_audio/signal_processing/cross_correlation.c"
  "${TGVOIP_DIR}/webrtc_dsp/common_audio/signal_processing/division_operations.c"
  "${TGVOIP_DIR}/webrtc_dsp/common_audio/signal_processing/auto_correlation.c"
  "${TGVOIP_DIR}/webrtc_dsp/common_audio/signal_processing/get_scaling_square.c"
  "${TGVOIP_DIR}/webrtc_dsp/common_audio/signal_processing/resample.c"
  "${TGVOIP_DIR}/webrtc_dsp/common_audio/signal_processing/min_max_operations.c"
  "${TGVOIP_DIR}/webrtc_dsp/common_audio/signal_processing/refl_coef_to_lpc.c"
  "${TGVOIP_DIR}/webrtc_dsp/common_audio/signal_processing/filter_ar.c"
  "${TGVOIP_DIR}/webrtc_dsp/common_audio/signal_processing/vector_scaling_operations.c"
  "${TGVOIP_DIR}/webrtc_dsp/common_audio/signal_processing/resample_fractional.c"
  "${TGVOIP_DIR}/webrtc_dsp/common_audio/signal_processing/real_fft.c"
  "${TGVOIP_DIR}/webrtc_dsp/common_audio/signal_processing/ilbc_specific_functions.c"
  "${TGVOIP_DIR}/webrtc_dsp/common_audio/signal_processing/complex_bit_reverse.c"
  "${TGVOIP_DIR}/webrtc_dsp/common_audio/signal_processing/randomization_functions.c"
  "${TGVOIP_DIR}/webrtc_dsp/common_audio/signal_processing/copy_set_operations.c"
  "${TGVOIP_DIR}/webrtc_dsp/common_audio/signal_processing/resample_by_2.c"
  "${TGVOIP_DIR}/webrtc_dsp/common_audio/signal_processing/get_hanning_window.c"
  "${TGVOIP_DIR}/webrtc_dsp/common_audio/signal_processing/resample_48khz.c"
  "${TGVOIP_DIR}/webrtc_dsp/common_audio/signal_processing/spl_inl.c"
  "${TGVOIP_DIR}/webrtc_dsp/common_audio/signal_processing/spl_sqrt.c"
  "${TGVOIP_DIR}/webrtc_dsp/common_audio/vad/vad_sp.c"
  "${TGVOIP_DIR}/webrtc_dsp/common_audio/vad/vad.cc"
  "${TGVOIP_DIR}/webrtc_dsp/common_audio/vad/webrtc_vad.c"
  "${TGVOIP_DIR}/webrtc_dsp/common_audio/vad/vad_filterbank.c"
  "${TGVOIP_DIR}/webrtc_dsp/common_audio/vad/vad_core.c"
  "${TGVOIP_DIR}/webrtc_dsp/common_audio/vad/vad_gmm.c"
)
if (${ANDROID_ABI} STREQUAL "armeabi-v7a" OR ${ANDROID_ABI} STREQUAL "arm64-v8a")
  target_sources(tgvoip PRIVATE
    "${TGVOIP_DIR}/webrtc_dsp/modules/audio_processing/ns/nsx_core_neon.c"
    "${TGVOIP_DIR}/webrtc_dsp/modules/audio_processing/aec/aec_core_neon.cc"
    "${TGVOIP_DIR}/webrtc_dsp/modules/audio_processing/aecm/aecm_core_neon.cc"
    "${TGVOIP_DIR}/webrtc_dsp/modules/audio_processing/utility/ooura_fft_neon.cc"
    "${TGVOIP_DIR}/webrtc_dsp/common_audio/fir_filter_neon.cc"
    "${TGVOIP_DIR}/webrtc_dsp/common_audio/resampler/sinc_resampler_neon.cc"
    "${TGVOIP_DIR}/webrtc_dsp/common_audio/signal_processing/downsample_fast_neon.c"
    "${TGVOIP_DIR}/webrtc_dsp/common_audio/signal_processing/min_max_operations_neon.c"
    "${TGVOIP_DIR}/webrtc_dsp/common_audio/signal_processing/cross_correlation_neon.c"
  )
endif()
if (${ANDROID_ABI} STREQUAL "armeabi-v7a")
  target_sources(tgvoip PRIVATE
    "${TGVOIP_DIR}/webrtc_dsp/common_audio/third_party/spl_sqrt_floor/spl_sqrt_floor_arm.S"
    "${TGVOIP_DIR}/webrtc_dsp/common_audio/signal_processing/complex_bit_reverse_arm.S"
    "${TGVOIP_DIR}/webrtc_dsp/common_audio/signal_processing/filter_ar_fast_q12_armv7.S"
  )
endif()
if (${ANDROID_ABI} STREQUAL "x86" OR ${ANDROID_ABI} STREQUAL "x86_64")
  target_sources(tgvoip PRIVATE
    "${TGVOIP_DIR}/webrtc_dsp/modules/audio_processing/aec/aec_core_sse2.cc"
    "${TGVOIP_DIR}/webrtc_dsp/modules/audio_processing/utility/ooura_fft_sse2.cc"
    "${TGVOIP_DIR}/webrtc_dsp/common_audio/fir_filter_sse.cc"
    "${TGVOIP_DIR}/webrtc_dsp/common_audio/resampler/sinc_resampler_sse.cc"
  )
endif()
target_compile_definitions(tgvoip PRIVATE
  TGVOIP_USE_CUSTOM_CRYPTO
  TGVOIP_HAS_CONFIG
  TGVOIP_NO_VIDEO
  TGVOIP_NO_GROUP_CALLS
  TGVOIP_PACKAGE_PATH="org/thunderdog/challegram/voip"
  TGVOIP_PEER_TAG_VARIABLE_NAME="peerTag"
  TGVOIP_ENDPOINT_CLASS="org/drinkless/td/libcore/telegram/TdApi$CallServer"

  USE_KISS_FFT
  WEBRTC_APM_DEBUG_DUMP=0
  WEBRTC_POSIX
  WEBRTC_ANDROID
  FIXED_POINT
  WEBRTC_NS_FLOAT
  __STDC_LIMIT_MACROS
)
target_include_directories(tgvoip PRIVATE
  "${OPUS_DIR}/include"
  "${TGVOIP_DIR}/webrtc_dsp"
  .
)
target_compile_options(tgvoip PRIVATE
  -frtti -fexceptions -finline-functions -ffast-math -fno-strict-aliasing -Wno-unknown-pragmas
)
target_link_libraries(tgvoip PUBLIC log OpenSLES)

# usrsctp

set(USRSCTP_DIR "${THIRDPARTY_DIR}/usrsctp/usrsctplib")

add_library(usrsctp STATIC
  "${USRSCTP_DIR}/netinet/sctp_asconf.c"
  "${USRSCTP_DIR}/netinet/sctp_auth.c"
  "${USRSCTP_DIR}/netinet/sctp_bsd_addr.c"
  "${USRSCTP_DIR}/netinet/sctp_callout.c"
  "${USRSCTP_DIR}/netinet/sctp_cc_functions.c"
  "${USRSCTP_DIR}/netinet/sctp_crc32.c"
  "${USRSCTP_DIR}/netinet/sctp_indata.c"
  "${USRSCTP_DIR}/netinet/sctp_input.c"
  "${USRSCTP_DIR}/netinet/sctp_output.c"
  "${USRSCTP_DIR}/netinet/sctp_pcb.c"
  "${USRSCTP_DIR}/netinet/sctp_peeloff.c"
  "${USRSCTP_DIR}/netinet/sctp_sha1.c"
  "${USRSCTP_DIR}/netinet/sctp_ss_functions.c"
  "${USRSCTP_DIR}/netinet/sctp_sysctl.c"
  "${USRSCTP_DIR}/netinet/sctp_timer.c"
  "${USRSCTP_DIR}/netinet/sctp_userspace.c"
  "${USRSCTP_DIR}/netinet/sctp_usrreq.c"
  "${USRSCTP_DIR}/netinet/sctputil.c"
  "${USRSCTP_DIR}/netinet6/sctp6_usrreq.c"
  "${USRSCTP_DIR}/user_environment.c"
  "${USRSCTP_DIR}/user_mbuf.c"
  "${USRSCTP_DIR}/user_recv_thread.c"
  "${USRSCTP_DIR}/user_socket.c"
)
target_compile_definitions(usrsctp PRIVATE
  __Userspace__
  SCTP_SIMPLE_ALLOCATOR
  SCTP_PROCESS_LEVEL_LOCKS
)
target_include_directories(usrsctp PUBLIC
  "${USRSCTP_DIR}"
)

# srtp

set(SRTP_DIR "${THIRDPARTY_DIR}/libsrtp")

# libsrtp version matches the one used in Chromium:
# https://chromium.googlesource.com/chromium/deps/libsrtp/+/5b7c744/LIBSRTP_VERSION
# TODO: extract commit hash dynamically instead of hardcoding current $(git rev-parse HEAD)
set(SRTP_COMMIT_HASH "860492290f7d1f25e2bd45da6471bfd4cd4d7add")

add_library(srtp STATIC
  "${SRTP_DIR}/crypto/cipher/aes_gcm_ossl.c"
  "${SRTP_DIR}/crypto/cipher/aes_icm_ossl.c"
  "${SRTP_DIR}/crypto/cipher/cipher.c"
  "${SRTP_DIR}/crypto/cipher/null_cipher.c"
  "${SRTP_DIR}/crypto/hash/auth.c"
  "${SRTP_DIR}/crypto/hash/hmac_ossl.c"
  "${SRTP_DIR}/crypto/hash/null_auth.c"
  "${SRTP_DIR}/crypto/kernel/alloc.c"
  "${SRTP_DIR}/crypto/kernel/crypto_kernel.c"
  "${SRTP_DIR}/crypto/kernel/err.c"
  "${SRTP_DIR}/crypto/kernel/key.c"
  "${SRTP_DIR}/crypto/math/datatypes.c"
  "${SRTP_DIR}/crypto/math/stat.c"
  "${SRTP_DIR}/crypto/replay/rdb.c"
  "${SRTP_DIR}/crypto/replay/rdbx.c"
  "${SRTP_DIR}/crypto/replay/ut_sim.c"
  "${SRTP_DIR}/srtp/ekt.c"
  "${SRTP_DIR}/srtp/srtp.c"
)
# config.h options match the ones used in Chromium:
# https://chromium.googlesource.com/chromium/deps/libsrtp/+/5b7c744/BUILD.gn
target_compile_definitions(srtp PRIVATE
  PACKAGE_VERSION="${SRTP_COMMIT_HASH}"
  PACKAGE_STRING="${SRTP_COMMIT_HASH}"

  HAVE_CONFIG_H
  OPENSSL
  GCM
  HAVE_STDLIB_H
  HAVE_STRING_H
  HAVE_STDINT_H
  HAVE_INTTYPES_H
  HAVE_INT16_T
  HAVE_INT32_T
  HAVE_INT8_T
  HAVE_UINT16_T
  HAVE_UINT32_T
  HAVE_UINT64_T
  HAVE_UINT8_T
  HAVE_ARPA_INET_H
  HAVE_SYS_TYPES_H
  HAVE_UNISTD_H

  HAVE_ARPA_INET_H
  HAVE_NETINET_IN_H
  HAVE_SYS_TYPES_H
  HAVE_UNISTD_H
)
target_include_directories(srtp PRIVATE
  "${STUB_DIR}"
)
target_link_libraries(srtp PUBLIC
  usrsctp ssl
)
target_include_directories(srtp PUBLIC
  "${SRTP_DIR}/include"
  "${SRTP_DIR}/crypto/include"
)