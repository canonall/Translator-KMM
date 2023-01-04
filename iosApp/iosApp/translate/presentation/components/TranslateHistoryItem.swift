//
//  TranslateHistoryItem.swift
//  iosApp
//
//  Created by Can Önal on 04.01.23.
//  Copyright © 2023 orgName. All rights reserved.
//

import SwiftUI
import shared

struct TranslateHistoryItem: View {
    let uiHistoryItem: UiHistoryItem
    let onClick: () -> Void
    
    var body: some View {
        Button(action: onClick) {
            VStack(alignment: .leading) {
                HStack {
                    SmallLanguageItem(language: uiHistoryItem.fromLanguage)
                        .padding(.trailing)
                    Text(uiHistoryItem.fromText)
                        .foregroundColor(.lightBlue)
                        .font(.body)
                }
                .frame(maxWidth: .infinity, alignment: .leading)
                .padding(.bottom)
                
                HStack {
                    SmallLanguageItem(language: uiHistoryItem.toLanguage)
                        .padding(.trailing)
                    Text(uiHistoryItem.toText)
                        .foregroundColor(.onSurface)
                        .font(.body.weight(.semibold))
                }
                .frame(maxWidth: .infinity, alignment: .leading)
            }
            .frame(maxWidth: .infinity)
            .padding()
            .gradientSurface()
            .cornerRadius(15)
            .shadow(radius: 4)
        }
    }
}

struct TranslateHistoryItem_Previews: PreviewProvider {
    static var previews: some View {
        TranslateHistoryItem(
            uiHistoryItem: UiHistoryItem.Companion().previewItem,
            onClick: {}
        )
        .preferredColorScheme(.dark)
        
        TranslateHistoryItem(
            uiHistoryItem: UiHistoryItem.Companion().previewItem,
            onClick: {}
        )
        .preferredColorScheme(.light)
    }
}
