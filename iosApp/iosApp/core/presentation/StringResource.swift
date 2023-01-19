//
//  StringResource.swift
//  iosApp
//
//  Created by Can Önal on 18.01.23.
//  Copyright © 2023 orgName. All rights reserved.
//

import Foundation
import shared

struct Resources {
    
    // MARK: - Resources
    
    static let string = SharedRes.strings.shared
    static let file = SharedRes.files.shared
}

extension StringResource {
    
    // MARK: - convenience init
    
    convenience init(_ resourceId: String) {
        self.init(resourceId: resourceId, bundle: SharedRes.strings.shared.nsBundle)
    }
    
    // MARK: - StringResource+localized

    var localized: String {
        desc().localized()
    }
    
    func localized(_ args: String...) -> String {
        format(args_: args).localized()
    }
}
