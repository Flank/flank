package ftl.reports


object HtmlReport {

    data class group(val key: String, val name: String, val startIndex: Int, val count: Int)
    data class item(val key: String, val name: String)

    fun run() {
//        export const dataGroups: IGroup[] = [
//        {
//            key: 'group-0',
//            name: 'AssigneeListPageTest#displaysStudentItems',
//            startIndex: 0,
//            count: 1,
//        },
//        {
//            key: 'group-1',
//            name: 'SpeedGraderCommentsPageTest#displaysAuthorName',
//            startIndex: 1,
//            count: 1,
//        }];
        val dataGroup = listOf(
                group("group-0",
                        "AssigneeListPageTest#displaysStudentItems",
                        0,
                        1),
                group("group-1",
                        "SpeedGraderCommentsPageTest#displaysAuthorName",
                        1,
                        1)
        )

//        export const dataItems: IItem[] = [
//        {
//            key: 'item-0',
//            name: 'android.support.test.espresso.NoMatchingViewException: No views in hierarchy found matching: (with text: is "Everyone" and has sibling: with id: com.instructure.teacher:id/assigneeSubtitleView)'
//        },
//        {
//            key: 'item-1',
//            name: 'java.lang.RuntimeException: Waited for the root of the view hierarchy to have window focus and not request layout for 10 seconds. If you specified a non default root matcher, it may be picking a root that never takes focus.'
//        }
//        ];

        val dataItems = listOf(
                item("item-0", "android.support.test.espresso.NoMatchingViewException: No views in hierarchy found matching: (with text: is \"Everyone\" and has sibling: with id: com.instructure.teacher:id/assigneeSubtitleView)"),
                item("item-1", "java.lang.RuntimeException: Waited for the root of the view hierarchy to have window focus and not request layout for 10 seconds. If you specified a non default root matcher, it may be picking a root that never takes focus.")
        )

//        bitrise/inline.html
//
//        var o=[{key:"group-0",name:"AssigneeListPageTest#displaysStudentItems",startIndex:0,count:1},{key:"group-1",name:"SpeedGraderCommentsPageTest#displaysAuthorName",startIndex:1,count:1}],r=[{key:"item-0",name:'android.support.test.espresso.NoMatchingViewException: No views in hierarchy found matching: (with text: is "Everyone" and has sibling: with id: com.instructure.teacher:id/assigneeSubtitleView)'},{key:"item-1",name:"java.lang.RuntimeException: Waited for the root of the view hierarchy to have window focus and not request layout for 10 seconds. If you specified a non default root matcher, it may be picking a root that never takes focus."}]
//
//         group: =[{key:"group-0",...]
//
//         item: =[{key:"item-0",...]
//
//        Regex search & replace with serialized JSON

        // TODO:
        // load inline.html
        // find known good starting point
        // replace with generated json
        // verify html still renders as expected
    }

    @JvmStatic
    fun main(args: Array<String>) {
        run()
    }
}
