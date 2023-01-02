//
//  SmallLanguageItem.swift
//  iosApp
//
//  Created by Can Önal on 02.01.23.
//  Copyright © 2023 orgName. All rights reserved.
//

import SwiftUI
import shared

struct SmallLanguageItem: View {
    var language: UiLanguage
    
    var body: some View {
        Image(uiImage: UIImage(named: language.imageName.lowercased())!)
            .resizable()
            .frame(width: 30, height: 30)
    }
}

struct SmallLanguageItem_Previews: PreviewProvider {
    static var previews: some View {
        SmallLanguageItem(language: UiLanguage.Companion().previewItem)
    }
}
