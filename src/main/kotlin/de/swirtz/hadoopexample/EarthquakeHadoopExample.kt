package de.swirtz.hadoopexample

import au.com.bytecode.opencsv.CSVParser
import org.apache.hadoop.io.IntWritable
import org.apache.hadoop.io.LongWritable
import org.apache.hadoop.io.Text
import org.apache.hadoop.mapreduce.Mapper
import org.apache.hadoop.mapreduce.Reducer
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class EarthquakeDataMapper : Mapper<LongWritable, Text, Text, IntWritable>() {
    private val dtf = DateTimeFormatter.ISO_DATE_TIME

    override fun map(key: LongWritable, value: Text, context: Context) {
        if (key.get() > 0) {
            with(CSVParser()) {
                parseLine(value.toString())[0].let {
                    val date = LocalDate.from(dtf.parse(it)).toString()
                    context.write(Text(date), IntWritable(1))
                }
            }
        }
    }
}

class EarthquakeDataReduce : Reducer<Text, IntWritable, Text, IntWritable>() {
    override fun reduce(key: Text, values: MutableIterable<IntWritable>, context: Context) {
        context.write(key, IntWritable(values.sumBy(IntWritable::get)))
    }
}

fun main(args: Array<String>) {
    awaitJob(verbose = true, removeOutDir = true) {
        jarByKClass = EarthquakeDataMapper::class
        mapperKClass = EarthquakeDataMapper::class
        reducerKClass = EarthquakeDataReduce::class
        //output like this: '2018-01-24	246'
        outputKeyKClass = Text::class
        outputValueKClass = IntWritable::class
        inputPath = "src/main/resources/in.csv"
        outputPath = "src/main/resources/out"
    }
}