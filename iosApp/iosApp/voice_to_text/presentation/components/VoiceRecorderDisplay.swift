//
//  VoiceRecorderDisplay.swift
//  iosApp
//
//  Created by Can Önal on 07.01.23.
//  Copyright © 2023 orgName. All rights reserved.
//

import SwiftUI

struct VoiceRecorderDisplay: View {
    var powerRatioList : [Double]
    
    var body: some View {
        Canvas { context, size in
            context.clip(to: Path(CGRect(origin: .zero, size: size)))
            
            let barWidth = 3.0
            let barCount = Int(size.width / Double(2 * barWidth))
            let defaultLevel = 0.05
            let reversedRatioList = powerRatioList
                .map { ratio in
                    min(max(defaultLevel, ratio), 1.0)
                }
                .suffix(barCount)
                .reversed()
            
            for (index, powerRatio) in reversedRatioList.enumerated() {
                let centerY  = CGFloat(size.height / 2.0)
                let yTopStart = CGFloat(centerY - centerY * powerRatio)
                var path = Path()
                
                path.addRoundedRect(
                    in: CGRect(
                        x: CGFloat(size.width) - CGFloat(index) * 2.0 * barWidth,
                        y: yTopStart,
                        width: barWidth,
                        height: (centerY - yTopStart * 2.0)
                    ),
                    cornerSize: CGSize(width: 10.0, height: 10.0)
                )
                context.fill(path, with: .color(.primaryColor))
            }
        }
        .gradientSurface()
        .cornerRadius(20)
        .padding(.horizontal, 15)
        .padding(.vertical, 5)
        .shadow(radius: 4)
    }
}
