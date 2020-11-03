import XCTest

class UITests: XCTestCase {

    func test1() {
        XCTAssertEqual("", "")
    }
    
    func test2() {
        XCTAssertEqual("", "")
    }
    
    func test3() {
        XCTAssertEqual("", "")
    }
    
    func test1ENLocale() {
        XCTAssert(Locale.current.identifier.contains("en"))
    }
    
    func test1PLLocale() {
        XCTAssert(Locale.current.identifier.contains("pl"))
    }
}
