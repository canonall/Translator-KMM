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
    private let translateUseCase: TranslateUseCase
    private let historyDataSource: HistoryDataSource
    private let parser: any VoiceToTextParser
    
    @ObservedObject var viewModel: IOSTranslateViewModel
    
    init(translateUseCase: TranslateUseCase, historyDataSource: HistoryDataSource, parser: VoiceToTextParser) {
        self.translateUseCase = translateUseCase
        self.historyDataSource = historyDataSource
        self.parser = parser
        self.viewModel = IOSTranslateViewModel(translateUseCase: translateUseCase, historyDataSource: historyDataSource )
    }
    
    var body: some View {
        ZStack {
            List {
                Group {
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
                    
                    TranslateTextField(
                        fromText: Binding(
                            get: { viewModel.state.fromText },
                            set: {
                                viewModel.onEvent(event: TranslateEvent.ChangeTranslationText(text: $0))
                            }
                        ),
                        toText: viewModel.state.toText,
                        isTranslating: viewModel.state.isTranslating,
                        fromLanguage: viewModel.state.fromLanguage,
                        toLanguage: viewModel.state.toLanguage,
                        onTranslateEvent: { event in
                            viewModel.onEvent(event: event)
                        }
                    )
                    
                    if !viewModel.state.history.isEmpty {
                        Text(Resources.string.history.localized)
                            .font(.title)
                            .bold()
                            .frame(maxWidth: .infinity, alignment: .leading)
                    }
                    
                    ForEach(viewModel.state.history, id: \.self.id) { uiHistoryItem in
                        TranslateHistoryItem(
                            uiHistoryItem: uiHistoryItem,
                            onClick: {
                                viewModel.onEvent(event: TranslateEvent.SelectHistoryItem(item: uiHistoryItem))
                            }
                        )
                    }
                }
                .listRowSeparator(.hidden)
                .listRowBackground(Color.background)
            }
            .listStyle(.plain)
            .buttonStyle(.plain)
            
            VStack {
                Spacer()
                NavigationLink(
                    destination: VoiceToTextScreen(
                        onResult: { spokenText in
                            viewModel.onEvent(event: TranslateEvent.SubmitVoiceResult(result: spokenText))
                        },
                        parser: parser,
                        languageCode: viewModel.state.fromLanguage.language.languageCode)
                ) {
                    ZStack {
                        Circle()
                            .foregroundColor(.primaryColor)
                            .padding()
                        Image(uiImage: UIImage(named: "mic")!)
                            .foregroundColor(.onPrimary)
                            .accessibilityIdentifier("Record audio")
                    }
                    .frame(maxWidth: 100, maxHeight: 100)
                }
            }
        }
        .onAppear {
            viewModel.startObserving()
        }
        .onDisappear {
            viewModel.dispose()
        }
    }
}
