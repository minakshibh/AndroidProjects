package com.equiworx.model;

public class LessonDetails {
String	block_out_time_for_fullday,
    block_out_time_for_halfday,
    no_of_lessons,
    lesson_date;

public String getBlock_out_time_for_fullday() {
	return block_out_time_for_fullday;
}

public void setBlock_out_time_for_fullday(String block_out_time_for_fullday) {
	this.block_out_time_for_fullday = block_out_time_for_fullday;
}

public String getBlock_out_time_for_halfday() {
	return block_out_time_for_halfday;
}

public void setBlock_out_time_for_halfday(String block_out_time_for_halfday) {
	this.block_out_time_for_halfday = block_out_time_for_halfday;
}

public String getNo_of_lessons() {
	return no_of_lessons;
}

public void setNo_of_lessons(String no_of_lessons) {
	this.no_of_lessons = no_of_lessons;
}

public String getLesson_date() {
	return lesson_date;
}

public void setLesson_date(String lesson_date) {
	this.lesson_date = lesson_date;
}
}
