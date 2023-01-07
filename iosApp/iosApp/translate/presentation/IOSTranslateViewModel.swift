//
//  IOSTranslateViewModel.swift
//  iosApp
//
//  Created by Can Önal on 02.01.23.
//  Copyright © 2023 orgName. All rights reserved.
//

import Foundation
import shared

extension TranslateScreen {
    @MainActor class IOSTranslateViewModel: ObservableObject {
        private let translateUseCase: TranslateUseCase
        private let historyDataSource: HistoryDataSource
        private let viewModel: TranslateViewModel
        private var handle: DisposableHandle?
        
        // kotlin's default arguments don't work here
        // so, we have to create TranslateState instance
        @Published var state: TranslateState = TranslateState(
            fromText: "",
            toText: nil,
            isTranslating: false,
            fromLanguage: UiLanguage(language: .english, imageName: "english"),
            toLanguage: UiLanguage(language: .german, imageName: "german"),
            isChoosingFromLanguage: false,
            isChoosingToLanguage: false,
            error: nil,
            history: []
        )
        
        init(translateUseCase: TranslateUseCase, historyDataSource: HistoryDataSource) {
            self.translateUseCase = translateUseCase
            self.historyDataSource = historyDataSource
            self.viewModel = TranslateViewModel(translateUseCase: translateUseCase, historyDataSource: historyDataSource, coroutineScope: nil)
        }
        
        func onEvent(event: TranslateEvent) {
            self.viewModel.onEvent(event: event)
        }
        
        func startObserving() {
            handle = viewModel.state.subscribe(onCollect: { state in
                if let state = state {
                    self.state = state
                }
            })
        }
        
        func dispose() {
            handle?.dispose()
        }
    }
}
