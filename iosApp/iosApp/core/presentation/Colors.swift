//
//  Colors.swift
//  iosApp
//
//  Created by Can Önal on 02.01.23.
//  Copyright © 2023 orgName. All rights reserved.
//

import Foundation
import SwiftUI
import shared

extension Color {
    init(hex: Int64, alpha: Double = 1) {
        self.init(
            .sRGB,
            red: Double((hex >> 16) & 0xff) / 255,
            green: Double((hex >> 08) & 0xff) / 255,
            blue: Double((hex >> 00) & 0xff) / 255,
            opacity: alpha
        )
    }
    
    private static let colors = Colors()
    static let lightBlue = Color(hex: colors.LightBlue)
    static let lightBlueGrey = Color(hex: colors.LightBlueGrey)
    static let accentViolet = Color(hex: colors.AccentViolet)
    static let textBlack = Color(hex: colors.TextBlack)
    static let darkGrey = Color(hex: colors.DarkGrey)
    
    // if the theme is light, uses light
    // if the theme is dark, uses dark
    // beacuse of the traits.userInterfaceStyle defined below
    // which is called when theme changes
    static let primary = Color(light: .accentViolet, dark: .accentViolet)
    static let background = Color(light: .lightBlueGrey, dark: .darkGrey)
    static let onPrimary = Color(light: .white, dark: .white)
    static let onBackground = Color(light: .textBlack, dark: .white)
    static let surface = Color(light: .white, dark: .darkGrey)
    static let onSurface = Color(light: .black, dark: .white)
}

private extension Color {
    init(light: Self, dark: Self) {
        self.init(uiColor: UIColor(light: UIColor(light), dark: UIColor(dark)))
    }
}

private extension UIColor {
    // for structs we don't need to define convenience
    // but for classes we need, and it is like secondary constructor
    convenience init(light: UIColor, dark: UIColor) {
        self.init { traits in
            switch traits.userInterfaceStyle {
            case .light, .unspecified:
                return light
            case .dark:
                return dark
            @unknown default:
                return light
            }
        }
    }
}
