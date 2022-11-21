package it.unibo.oop.workers02;

import java.util.ArrayList;
import java.util.List;

public class MultiThreadedSumMatrix implements SumMatrix{
    public int nthread;
    public double sum;

    public MultiThreadedSumMatrix(final int nthread){
        this.nthread=nthread;
    }



    @Override
    public double sum(double [][] matrix){
        final int size = matrix.length / nthread + matrix.length % nthread;
        List<MultiThreadedSumMatrix.Worker> _workers=new ArrayList<>();
        for (int start = 0; start < matrix.length; start += size) {
            _workers.add(new Worker(matrix, start, size));
        }

        _workers.forEach((worker)->worker.start());
        _workers.forEach((worker)-> {
            try{
                worker.join();
                this.sum+=worker.getResult();
            }catch(Exception e){
                System.out.println(e.getMessage());
            }
        });
        return this.sum;
    }

    private static class Worker extends Thread{
        private double [][]matrix;
        private final int start;
        private final int stop;
        private double result;

        Worker(final double[][]matrix,final int start,final int stop){
            super();
            this.matrix=matrix;
            this.start=start;
            this.stop=stop;
        }

        @Override
        public void run(){
            for (int i = this.start; i < matrix.length && i < this.start + this.stop; i++) {
                for (final double d : this.matrix[i]) {
                    this.result += d;
                }
            }
        }

        public double getResult(){
            return this.result;
        }
    }
}
