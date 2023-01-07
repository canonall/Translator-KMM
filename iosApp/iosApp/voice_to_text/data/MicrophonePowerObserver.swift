//
//  MicrophonePowerObserver.swift
//  iosApp
//
//  Created by Can Önal on 07.01.23.
//  Copyright © 2023 orgName. All rights reserved.
//

import Foundation
import Speech
import Combine

class MicrophonePowerObserver: ObservableObject {
    private var cancellable: AnyCancellable? = nil
    private var audioRecorder: AVAudioRecorder? = nil
    private let powerRatioEmissionsPerSecond = 20.0
    
    @Published private(set) var micPowerRatio = 0.0
    
    func startObserving() {
        do {
            let recorderSettings: [String: Any] = [
                AVFormatIDKey: NSNumber(value: kAudioFormatAppleLossless),
                AVNumberOfChannelsKey: 1
            ]
            
            let recorder = try AVAudioRecorder(url: URL(fileURLWithPath: "/dev/null", isDirectory: true), settings: recorderSettings)
            recorder.isMeteringEnabled = true
            recorder.record()
            self.audioRecorder = recorder
            
            self.cancellable = Timer.publish(
                every: 1.0 / powerRatioEmissionsPerSecond,
                tolerance: 1.0 / powerRatioEmissionsPerSecond,
                on: .main,
                in: .common
            )
            .autoconnect()
            // if the class is destroyed but the sink is still alive
            // with the [weak self], self will be set to nil, otherwise
            // memory leak can occur
            .sink { [weak self] _ in
                recorder.updateMeters()
                
                let powerOffset = recorder.averagePower(forChannel: 0)
                if powerOffset < -50 {
                    self?.micPowerRatio = 0.0
                } else {
                    let normalizedOffset = CGFloat(50 + powerOffset) / 50
                    self?.micPowerRatio = normalizedOffset
                }
            }
        } catch {
            print("An error occured when observing microphone power: \(error.localizedDescription)")
        }
    }
    
    func release() {
        cancellable = nil
        audioRecorder?.stop()
        audioRecorder = nil
        micPowerRatio = 0.0
    }
}
