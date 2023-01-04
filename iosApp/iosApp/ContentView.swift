import SwiftUI
import shared

struct ContentView: View {
    
    private let appModule = AppModule()
    
    var body: some View {
        ZStack {
            Color.background
                .ignoresSafeArea()
            TranslateScreen(
                translateUseCase: appModule.translateUseCase,
                historyDataSource: appModule.historyDataSource
            )
        }
        .hideNavigationBar
    }
}

struct ContentView_Previews: PreviewProvider {
    static var previews: some View {
        ContentView()
    }
}
