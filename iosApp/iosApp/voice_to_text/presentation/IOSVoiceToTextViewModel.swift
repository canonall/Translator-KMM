//
//  IOSVoiceToTextViewModel.swift
//  iosApp
//
//  Created by Can Önal on 07.01.23.
//  Copyright © 2023 orgName. All rights reserved.
//

import Foundation
import shared
import Combine

@MainActor class IOSVoiceToTextViewModel: ObservableObject {
    private var parser: any VoiceToTextParser
    private let languageCode:  String
    private let viewModel: VoiceToTextViewModel
    private var handle: DisposableHandle?
    
    @Published var state  = VoiceToTextState(
        powerRatios: [],
        spokenText: "",
        canRecord: false,
        recordError: nil,
        displayState: nil
    )
    
    init(parser: VoiceToTextParser, languageCode: String) {
        self.parser = parser
        self.languageCode = languageCode
        self.viewModel = VoiceToTextViewModel(parser: parser, coroutineScope: nil)
    }
    
    func onEvent(event: VoiceToTextEvent) {
        viewModel.onEvent(event: event)
    }
    
    func startObserving() {
        handle = viewModel.state.subscribe{ [weak self] state in
            if let state {
                self?.state = state
            }
        }
    }
    
    func dispose() {
        handle?.dispose()
        onEvent(event: VoiceToTextEvent.Reset())
    }
}
