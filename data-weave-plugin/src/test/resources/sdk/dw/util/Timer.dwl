%dw 2.0

/**
* Represents a start/end time meassurement
*/
type TimeMeasurement<T> =  {start: DateTime, result: T, end: DateTime}

/**
* Represents a time taken by a function call
*/
type DurationMeasurement<T> = {time: Number, result: T}

/**
* Returns the current time in milliseconds
*/
fun currentMilliseconds(): Number =
    toMilliseconds(now())


/**
* Returns the representation of the specified DateTime in milliseconds
*/
fun toMilliseconds(date:DateTime): Number =
    date as Number {unit: "milliseconds"}

/**
* Executes the function and returns an object with the taken time in milliseconds with the result of the function
*/
fun duration<T>(valueToMeasure: ()-> T): DurationMeasurement<T> = do {
    var timeResult = time(valueToMeasure)
    ---
    {
        time: toMilliseconds(timeResult.end) - toMilliseconds(timeResult.start),
        result: timeResult.result
    }
}

/**
* Executes the specified function and returns an object with the start time and end time with the result of the function
*/
fun time<T>(valueToMeasure: ()-> T): TimeMeasurement<T> = do {
    var statTime = now()
    var result = valueToMeasure()
    var endTime = now()
    ---
    {
        start: statTime,
        result: result,
        end: endTime
    }

}

