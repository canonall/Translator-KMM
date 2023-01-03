//
//  TranslateScreen.swift
//  iosApp
//
//  Created by Can Önal on 02.01.23.
//  Copyright © 2023 orgName. All rights reserved.
//

import SwiftUI
import shared

struct TranslateScreen: View {
    private var translateUseCase: TranslateUseCase
    private var historyDataSource: HistoryDataSource
    @ObservedObject var viewModel: IOSTranslateViewModel
    
    init(translateUseCase: TranslateUseCase, historyDataSource: HistoryDataSource) {
        self.translateUseCase = translateUseCase
        self.historyDataSource = historyDataSource
        self.viewModel = IOSTranslateViewModel(translateUseCase: translateUseCase, historyDataSource: historyDataSource )
    }
    
    var body: some View {
        ZStack {
            List {
                HStack(alignment: .center) {
                    LanguageDropdown(
                        language: viewModel.state.fromLanguage,
                        isOpen: viewModel.state.isChoosingFromLanguage,
                        selectLanguage: { uiLanguage in
                            viewModel.onEvent(event: TranslateEvent.ChooseFromLanguage(uiLanguage: uiLanguage))
                        }
                    )
                    Spacer()
                    SwapLanguageButton(onClick: {
                        viewModel.onEvent(event: TranslateEvent.SwapLanguages())
                    })
                    Spacer()
                    LanguageDropdown(
                        language: viewModel.state.toLanguage,
                        isOpen: viewModel.state.isChoosingToLanguage,
                        selectLanguage: { uiLanguage in
                            viewModel.onEvent(event: TranslateEvent.ChooseToLanguage(uiLanguage: uiLanguage))
                        }
                    )
                }
                .listRowSeparator(.hidden)
                .listRowBackground(Color.background)
            }
            .listStyle(.plain)
            .buttonStyle(.plain)
        }
        .onAppear {
            viewModel.startObserving()
        }
        .onDisappear {
            viewModel.dispose()
        }
    }
}
