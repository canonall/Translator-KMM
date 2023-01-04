//
//  ViewExtension.swift
//  iosApp
//
//  Created by Can Önal on 04.01.23.
//  Copyright © 2023 orgName. All rights reserved.
//

import SwiftUI

extension View {
    @ViewBuilder
    var hideNavigationBar: some View {
        if #available(iOS 16, *) {
            self.toolbar(.hidden)
        } else {
            self.navigationBarHidden(true)
        }
    }
}
