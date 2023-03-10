//
//  TranslateTextField.swift
//  iosApp
//
//  Created by Can Önal on 03.01.23.
//  Copyright © 2023 orgName. All rights reserved.
//

import SwiftUI
import shared
import UniformTypeIdentifiers

struct TranslateTextField: View {
    @Binding var fromText: String
    let toText: String?
    let isTranslating: Bool
    let fromLanguage: UiLanguage
    let toLanguage: UiLanguage
    let onTranslateEvent: (TranslateEvent) -> Void
    
    
    var body: some View {
        if toText == nil || isTranslating {
            IdleTextField(
                fromText: $fromText,
                isTranslating: isTranslating,
                onTranslateEvent: onTranslateEvent
            )
            .gradientSurface()
            .cornerRadius(15)
            .animation(.easeInOut, value: isTranslating)
            .shadow(radius: 4)
        } else {
            TranslatedTextField(
                fromText: fromText,
                toText: toText ?? "",
                fromLanguage: fromLanguage,
                toLanguage: toLanguage,
                onTranslateEvent: onTranslateEvent
            )
            .padding()
            .gradientSurface()
            .cornerRadius(15)
            .animation(.easeInOut, value: isTranslating)
            .shadow(radius: 4)
            .onTapGesture {
                onTranslateEvent(TranslateEvent.EditTranslation())
            }
        }
    }
}

private extension TranslateTextField {
    struct IdleTextField: View {
        @Binding var fromText: String
        let isTranslating: Bool
        let onTranslateEvent: (TranslateEvent) -> Void
        
        var body: some View {
            TextEditor(text: $fromText)
                .frame(
                    maxWidth: .infinity,
                    minHeight: 200,
                    alignment: .topLeading
                )
                .padding()
                .foregroundColor(Color.onSurface)
                .overlay(alignment: .bottomTrailing) {
                    ProgressButton(
                        text: Resources.string.translate.localized,
                        isLoading: isTranslating,
                        onClick: {
                            onTranslateEvent(TranslateEvent.Translate())
                        }
                    )
                    .padding([.bottom, .trailing])
                }
                .onAppear {
                    UITextView.appearance().backgroundColor = .clear
                }
        }
    }
    
    struct TranslatedTextField: View {
        let fromText: String
        let toText: String
        let fromLanguage: UiLanguage
        let toLanguage: UiLanguage
        let onTranslateEvent: (TranslateEvent) -> Void
        
        private let textToSpeech = TextToSpeech()
        
        var body: some View {
            VStack(alignment: .leading) {
                LanguageDisplay(language: fromLanguage)
                Text(fromText)
                    .foregroundColor(.onSurface)
                HStack {
                    Spacer()
                    Button {
                        UIPasteboard.general.setValue(fromText, forPasteboardType: UTType.plainText.identifier)
                    } label: {
                        Image(uiImage: UIImage(named: "copy")!)
                            .renderingMode(.template)
                            .foregroundColor(.lightBlue)
                    }
                    Button {
                        print("close")
                        onTranslateEvent(TranslateEvent.CloseTranslation())
                    } label: {
                        Image(systemName: "xmark")
                            .foregroundColor(.lightBlue)
                    }
                }
                Divider()
                    .padding()
                LanguageDisplay(language: toLanguage)
                    .padding(.bottom)
                Text(toText)
                    .foregroundColor(.onSurface)
                HStack {
                    Spacer()
                    Button {
                        UIPasteboard.general.setValue(toText, forPasteboardType: UTType.plainText.identifier)
                    } label: {
                        Image(uiImage: UIImage(named: "copy")!)
                            .renderingMode(.template)
                            .foregroundColor(.lightBlue)
                    }
                    Button {
                        textToSpeech.speak(
                            text: toText,
                            language: toLanguage.language.languageCode
                        )
                    } label: {
                        Image(systemName: "speaker.wave.2")
                            .foregroundColor(.lightBlue)
                    }
                }
            }
        }
    }
}

struct TranslateTextField_Previews: PreviewProvider {
    static var previews: some View {
        TranslateTextField(
            fromText: Binding(
                get: { "test" },
                set: { _ in }
            ),
            toText: "Translated Test",
            isTranslating: false,
            fromLanguage: UiLanguage.Companion().previewItem,
            toLanguage: UiLanguage(language: .english, imageName: "english"),
            onTranslateEvent: { _ in })
    }
}
