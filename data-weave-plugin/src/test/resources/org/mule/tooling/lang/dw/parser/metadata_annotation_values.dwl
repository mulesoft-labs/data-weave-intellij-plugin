
var test = @Label(value = "Object Label")
           @Description(value= "Object description")
          {
            @Description(value= "Test description") a @(@Label(value="123") name: @Label(value="123") "123"): @Label(value="Test value") "a"
          }

var addMetadataFromAnotherVar = @Label(value = "Override Label")
                                @TestKey(value = "Another key")
                                test
---
{
   objectMetadata: test.^,
   fieldValueMetadata: test.a.^ ,
   objectMetadataAddition: addMetadataFromAnotherVar.^,
   attributeMetadata: test.a.@.name.^
}