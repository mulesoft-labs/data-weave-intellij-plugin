%dw 2.0

import * from dw::extension::DataFormat

fun readNDJson(content: Binary, charset: String, settings: EmptySettings): Any = do {
    content as String {encoding: charset}
}

fun writeNDJson(content: Any, settings: EmptySettings): Binary = do {
  (content as Array
  map ((item, index) -> write(item , "application/json") as String)
  reduce ((item, accumulator) -> item ++ "\n" ++ accumulator))
  as Binary
}
@DataFormatExtension
var ndjson: DataFormat<EmptySettings, EmptySettings> = {
  acceptedMimeTypes: ["application/x-ndjson", "application/x-ldjson"],
  fileExtensions: [".ndjson", ".ldjson", ".ldj", ".jsonl"],
  reader: readNDJson,
  writer: writeNDJson
}
