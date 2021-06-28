/**
* This scripts needs the payload to be dialect and the vocabulary input to be the vocabulary
*/
%dw 2.0
output application/json

input payload application/yaml
input vocabulary application/yaml
import * from dw::core::Strings

fun rangeToType(range: String | Array) =
    range match {
            case is Array<String|Array> ->  $ map ((item, index) -> rangeToType(item))
            case "string" ->  {"type": "string"}
            case "number" ->  {"type": "number"}
            case "integer" -> {"type": "integer"}
            case "boolean" -> {"type": "boolean"}
            case is String -> {"\$ref": "#/definitions/$"}
    }

fun mapVocabulary(value: {propertyTerm?: String}) =
    value match {
        case is {propertyTerm: String} -> {
            description: vocabulary.propertyTerms[($.propertyTerm substringAfter ".")].description default "",
            title: vocabulary.propertyTerms[($.propertyTerm substringAfter ".")].displayName default ""
        }
        else -> {}
    }

fun mapPropertyValue(value: {}) =
//If it has mapTermKey it means it only has constrains to the values
    if(value.mapTermKey?) do {
        var term = payload.nodeMappings..[?($ is Object and $.propertyTerm == value.mapTermKey)][0]
        ---
        term match {
          case term is {"enum": Array<String>} -> {
            "type": "object",
            properties: {
                (term."enum" map (option) -> {
                    (option): mapPropertyValue((value - "mapTermKey"))
                })
            },
            (mapVocabulary(value))
          }
          else -> {
                "type": "object",
                "patternProperties": {
                    "^.*\$": mapPropertyValue((value - "mapTermKey"))
                },
                (mapVocabulary(value))
            }
        }

    }
    else if(value.allowMultiple default false) //The semantics of allowMultiple is that it can be the value of an Array of Values
        {
            "anyOf": [
                {
                    "type": "array",
                        items: [
                            mapPropertyValue(value - "allowMultiple")
                        ]
                },
                mapPropertyValue(value - "allowMultiple")
            ]
        }
    else
        {
            (rangeToType(value.range)),
            (value.&'enum'),
            (mapVocabulary(value))
        }

fun mappingToObjectType(m: {mapping: {}}) =
    {
        "type": "object",
        "properties":
                m.mapping mapObject ((value, key, index) -> {
                    (key): mapPropertyValue(value)
                })
    }

fun extendsToRef(e: {'extends': String}) =
    {"\$ref": "#/definitions/$(e.'extends')"}

fun amfToJsonSchemaDef(amfDef) =
    amfDef match  {
            case e is {'extends': String, mapping: {}} -> {
                'allOf': [
                    extendsToRef(e),
                    mappingToObjectType(e)
                ]
            }
            case e is {'extends': String} ->  extendsToRef(e)
            case m is {mapping: {}} -> mappingToObjectType(m)
            case u is {'union': Array} ->  {
                "anyOf": u.union map ((item, index) -> rangeToType(item))
            }
            else -> {}
        }
---
{
    "\$schema":  "http://json-schema.org/draft-07/schema#",
    "\$id": "http://mulesoft.com/rest-sdk.json",
    (
        amfToJsonSchemaDef(payload.nodeMappings[payload.documents.root.encodes])
    ),
    definitions: payload.nodeMappings
                    mapObject ((value, key, index) -> {
                        ((key): amfToJsonSchemaDef(value)) if(!(key ~= payload.documents.root.encodes))
                    }
    )
}