//
//  LanguageDropdown.swift
//  iosApp
//
//  Created by Can Önal on 02.01.23.
//  Copyright © 2023 orgName. All rights reserved.
//

import SwiftUI
import shared

struct LanguageDropdown: View {
    var language: UiLanguage
    var isOpen: Bool
    var selectLanguage: (UiLanguage) -> Void
    
    var body: some View {
        Menu {
            VStack {
                ForEach(UiLanguage.Companion().allLanguages, id: \.self.language.languageCode) { language in
                    LanguageDropdownItem(
                        language: language,
                        onClick: { selectLanguage(language) }
                    )
                }
            }
        } label: {
            SmallLanguageItem(language: language)
            Text(language.language.languageName)
                .foregroundColor(.lightBlue)
            Image(systemName: isOpen ? "chevron.up" : "chevron.down")
                .foregroundColor(.lightBlue)
        }
    }
}

struct LanguageDropdown_Previews: PreviewProvider {
    static var previews: some View {
        LanguageDropdown(language: UiLanguage.Companion().previewItem, isOpen: false, selectLanguage: {_ in })
    }
}
