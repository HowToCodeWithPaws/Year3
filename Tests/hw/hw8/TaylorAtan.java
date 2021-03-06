class TaylorAtan {

    /** А это нормальный комментарий... Метод гет некст рассчитывает коэффициент при нном слагаемом
     * ряда Тейлора для арктангенса. Знак отрицательный при четных н и положительный при нечетных,
     * в знаменателе находится выражение 2n-1. n соответственно равен передаваемому параметру currN.
     * Требования к н в том, что оно больше 1 (так как первое слагаемое обрабатывается раньше).
     * Результат не равен 0, потому что числитель никогда не 0, и знаменатель тоже.
     * Результат также лежит в границах от -1 до 1 невключительно.*/
    //@ requires currN > 1;
    //@ ensures \result != 0;
    //@ ensures ((\result < 1 && \result > 0) || (\result < 0 && \result > -1));
    //@ nullable code_java_math spec_java_math
    double getNext(int currN){
        int sign = currN % 2 == 0 ? -1 : 1;
        double fraction = currN*2 - 1;
        double next = sign/fraction;
        return next;
    }

    /** Основной метод в цикле вычисляет ряд Тейлора для арктангенса. На вход принимается аргумент,
     * который должен быть от -1 до 1 включительно, эпсилон - точность, цикл будет останавливаться,
     * когда при прибавлении нового слагаемого сумма изменяется меньше, чем на эпсилон, он соответственно
     * больше 0, максимальное количесвто итераций - от 1 до 100 не включая концы.
     * Далее мне стало ясно что какие-то постусловия доказать невыносимо сложно, потому что это
     * численный алгоритм, и если даже как будто в матанализе можно доказать, что ряд сходится,
     * заставить jml это сделать мне не удалось.
     * Поэтому из постусловий то, что при нулевом аргументе результат нулевой, и не доказанные
     * утверждения о том, что результат лежит в определенных границах (в случае только одной итерации это
     * будет просто аргумент, то есть от -1 до 1), и что он всегда одного знака с аргументом.
     * Кроме того у меня почему-то отказался ассершн на исключение при некорректных параметрах, хотя кажется
     * jml это умеет.*/
    //@ requires arg <= 1 && arg >= -1;
    //@ requires eps > 0;
    //@ requires maxiters > 1 && maxiters < 100;
    //@ ensures (arg == 0) ==> (\result == 0);
   // //@ ensures (\result * arg) >= 0;
   // //@ ensures (\result <= 1) && (\result >= -1);
   // //@ signals (IllegalArgumentException) ((arg > 1) || (arg < -1) || (eps <= 0) || (maxiters <= 1));
    //@ nullable code_java_math spec_java_math
    public double TaylorAtan(double arg, double eps, int maxiters) {
        if (arg > 1 || arg < -1 || eps <= 0 || maxiters <= 1) throw new IllegalArgumentException();

        if(arg == 0) return 0;

        double taylor = 0;
        int n = 2;
        double currArg = arg;
        double newArg = currArg;
        double new_taylor = currArg;

        /** В цикле обновляются значения переменных, вызывается функция для поиска следующего коэффициента,
         * вычисляется следующая степень и прибавляется к ряду. При этом условие на остановку цикла -
         * либо максимальное число итераций, либо точность. Инварианты таким образом это количество
         * совершенных итераций n, то, что аргументы всегда оказываются меньше 1 по модулю, а также
         * они всегда одного знака, и следующий аргумент по модулю меньше предыдущего.
         * Почему-то не получилось доказать то, что результат одного знака с аргументом, и какую-либо
         * его ограниченность. сложнее всего доказать то что и так ясно(*/
        //@ loop_invariant n > 1;
        //@ loop_invariant n <= maxiters;
        //@ loop_invariant currArg <= 1 && currArg >= -1;
        //@ loop_invariant newArg <= 1 && newArg >= -1;
        //@ loop_invariant ((newArg < 0) && (currArg < 0) && (newArg >= currArg)) || ((newArg > 0) && (currArg > 0) && (newArg <= currArg)) || ((newArg == 0) && (currArg ==0));
        ////@ loop_invariant (currArg <= 0) ==> (new_taylor <= 0);
        while ((n < maxiters) && (((new_taylor < taylor) & ((taylor - new_taylor) < eps)) || ((new_taylor > taylor) & ((new_taylor - taylor) < eps)))) {
            taylor = new_taylor;
            newArg = currArg * currArg * currArg;
            new_taylor = new_taylor + getNext(n) * newArg;

            n = n + 1;
            currArg = newArg;
        }

        return new_taylor;
    }
}
