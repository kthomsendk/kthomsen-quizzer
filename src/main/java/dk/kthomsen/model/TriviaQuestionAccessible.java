package dk.kthomsen.model;

import java.util.List;

/**
 * Created by Kenneth Thomsen on 25/05/2017.
 * Project: kthomsen-quizzer
 */
public interface TriviaQuestionAccessible {

    TriviaQuestion getQuestionByIndex(long index);

    TriviaQuestion getQuestionById(long id);

    TriviaQuestion getRandomQuestion();

    List<TriviaQuestion> getQuestionList(long offset);

    List<TriviaQuestion> getSpecifiedQuestionList(long... id);

    long getQuestionListSize();

}
