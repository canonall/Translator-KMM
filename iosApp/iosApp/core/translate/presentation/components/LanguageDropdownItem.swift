//
//  LanguageDropdownItem.swift
//  iosApp
//
//  Created by Can Önal on 02.01.23.
//  Copyright © 2023 orgName. All rights reserved.
//

import SwiftUI
import shared

struct LanguageDropdownItem: View {
    var language: UiLanguage
    var onClick: () -> Void
    
    var body: some View {
        Button(action: onClick) {
            HStack {
                if let image = UIImage(named: language.imageName.lowercased()) {
                    Image(uiImage: image)
                        .resizable()
                        .frame(width: 40, height: 40)
                        .padding(.trailing, 5)
                    Text(language.language.languageName)
                        .foregroundColor(.textBlack)
                }
            }
        }
    }
}

struct LanguageDropdownItem_Previews: PreviewProvider {
    static var previews: some View {
        LanguageDropdownItem(language: UiLanguage.Companion().previewItem, onClick: {})
    }
}
