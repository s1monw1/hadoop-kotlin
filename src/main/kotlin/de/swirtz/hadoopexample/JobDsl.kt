package de.swirtz.hadoopexample

import org.apache.hadoop.fs.Path
import org.apache.hadoop.mapred.JobConf
import org.apache.hadoop.mapreduce.Job
import org.apache.hadoop.mapreduce.Mapper
import org.apache.hadoop.mapreduce.Reducer
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat
import java.nio.file.Files
import java.nio.file.Paths
import kotlin.reflect.KClass


fun awaitJob(verbose: Boolean = false, removeOutDir: Boolean = false, init: CreateJob.() -> Unit) =
    with(CreateJob(removeOutDir)) {
        init()
        create().waitForCompletion(verbose)
    }

class CreateJob(private val deleteOut: Boolean = false) {
    lateinit var mapperKClass: KClass<out Mapper<*, *, *, *>>
    lateinit var reducerKClass: KClass<out Reducer<*, *, *, *>>
    lateinit var outputKeyKClass: KClass<*>
    lateinit var outputValueKClass: KClass<*>
    lateinit var jarByKClass: KClass<*>
    lateinit var inputPath: String
    lateinit var outputPath: String

    inline fun <reified L, reified R> outputClasses() {
        outputKeyKClass = L::class
        outputValueKClass = R::class
    }

    inline fun <reified L : Mapper<*, *, *, *>, reified R : Reducer<*, *, *, *>> mapreduceWith() {
        mapperKClass = L::class
        reducerKClass = R::class
    }

    fun create(): Job = Job(JobConf(jarByKClass.java)).apply {
        FileInputFormat.addInputPath(this, Path(inputPath))
        FileOutputFormat.setOutputPath(this, Path(outputPath))
        mapperClass = mapperKClass.java
        reducerClass = reducerKClass.java
        outputKeyClass = outputKeyKClass.java
        outputValueClass = outputValueKClass.java
        FileInputFormat.addInputPath(this, Path(inputPath))
        FileOutputFormat.setOutputPath(this, Path(outputPath))
        if (deleteOut) {
            Paths.get(outputPath).let {
                if (Files.exists(it)) {
                    Files.list(it).forEach { Files.deleteIfExists(it) }
                    Files.deleteIfExists(it)
                }
            }

        }
    }

}




