import SwiftUI
import shared

struct ContentView: View {
    
    private let appModule = AppModule()
    
    var body: some View {
        TranslateScreen(
            translateUseCase: appModule.translateUseCase,
            historyDataSource: appModule.historyDataSource
        )
    }
}

struct ContentView_Previews: PreviewProvider {
    static var previews: some View {
        ContentView()
    }
}
