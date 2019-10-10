ns foo http://test.com
---
{
    a: payload update {
        case .a.b -> 123
        case .a.b[0] -> 123
        case [-1] -> 123
        case .c."a-b" -> 123
        case .@c."a-b" -> 123
        case .@"c"."a-b" -> 123
        case .foo#c."a-b" -> upper($)
        case .@foo#c."a-b" -> upper($)
        case test at .foo#c."a-b" -> test
        case test at .foo#"c"."a-b" -> test
        case test at .@foo#c."a-b" -> test
        case test at .@foo#"c"."a-b" -> test
        case (test,index) at .@foo#"c"."a-b" -> test
        case (test,index) at .*foo if(index == 1) -> test
        case .c."a-b"! -> 123
        case .c."a-b".c! -> 123
        case (test,index) at .*foo! if(index == 1) -> test
        case .a[random()] -> 123
    }
}