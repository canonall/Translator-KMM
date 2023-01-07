//
//  VoiceToTextScreen.swift
//  iosApp
//
//  Created by Can Önal on 07.01.23.
//  Copyright © 2023 orgName. All rights reserved.
//

import SwiftUI
import shared

struct VoiceToTextScreen: View {
    @Environment(\.presentationMode) var presentation
    @ObservedObject var viewModel: IOSVoiceToTextViewModel
    
    private let onResult: (String) -> Void
    private let parser: any VoiceToTextParser
    private let languageCode: String
    
    init(onResult: @escaping (String) -> Void, parser: any VoiceToTextParser, languageCode: String) {
        self.onResult = onResult
        self.parser = parser
        self.languageCode = languageCode
        self.viewModel = IOSVoiceToTextViewModel(parser: parser, languageCode: languageCode)
    }
    
    var body: some View {
        VStack {
            Spacer()
            mainView
            Spacer()
            HStack {
                Spacer()
                VoiceRecorderButton(
                    displayState: viewModel.state.displayState ?? .waitingToTalk,
                    onClick: {
                        if viewModel.state.displayState != .displayingResults{
                            viewModel.onEvent(event: VoiceToTextEvent.ToggleRecording(languageCode: languageCode))
                        } else {
                            onResult(viewModel.state.spokenText)
                            self.presentation.wrappedValue.dismiss()
                        }
                    }
                )
                if viewModel.state.displayState == .displayingResults {
                    Button(action: {
                        viewModel.onEvent(event: VoiceToTextEvent.ToggleRecording(languageCode: languageCode))
                    }) {
                        Image(systemName: "arrow.clockwise")
                            .foregroundColor(.lightBlue)
                    }
                }
                Spacer()
            }
        }
        .onAppear {
            viewModel.startObserving()
        }
        .onDisappear {
            viewModel.dispose()
        }
        .background(Color.background)
    }
    
    var mainView: some View {
        if  let displayState = viewModel.state.displayState {
            switch displayState {
            case .waitingToTalk:
                return AnyView(
                    Text("Click record and start talking.")
                        .font(.title2)
                )
            case .displayingResults:
                return AnyView(
                    Text(viewModel.state.spokenText)
                        .font(.title2)
                )
            case .error:
                return AnyView(
                    Text(viewModel.state.recordError ?? "Unknown error")
                        .font(.title2)
                        .foregroundColor(.red)
                )
            case .speaking:
                return AnyView(
                    VoiceRecorderDisplay(powerRatioList: viewModel.state.powerRatios.map{ Double(truncating: $0)})
                        .frame(maxWidth: 100)
                        .padding()
                )
            default:
                return AnyView(EmptyView())
            }
        } else {
            return AnyView(EmptyView())
        }
    }
}
