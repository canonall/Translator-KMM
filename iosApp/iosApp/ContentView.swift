import SwiftUI
import shared

struct ContentView: View {
    
    let appModule: AppModule
    
    var body: some View {
        ZStack {
            Color.background
                .ignoresSafeArea()
            TranslateScreen(
                translateUseCase: appModule.translateUseCase,
                historyDataSource: appModule.historyDataSource,
                parser: appModule.voiceToTextParser
            )
        }
        .hideNavigationBar
    }
}
