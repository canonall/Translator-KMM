//
//  LanguageDisplay.swift
//  iosApp
//
//  Created by Can Önal on 03.01.23.
//  Copyright © 2023 orgName. All rights reserved.
//

import SwiftUI
import shared

struct LanguageDisplay: View {
    var language: UiLanguage
    
    var body: some View {
        HStack {
            SmallLanguageItem(language: language)
                .padding(.trailing, 5)
            Text(language.language.languageName)
                .foregroundColor(.lightBlue)
        }
    }
}

struct LanguageDisplay_Previews: PreviewProvider {
    static var previews: some View {
        LanguageDisplay(language: UiLanguage.Companion().previewItem)
    }
}
