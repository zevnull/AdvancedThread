package com.mytest.thread.lock;


import java.util.concurrent.TimeUnit;

public class Operations {
    private static final int WAIT_SEC = 5;

    public static void main(String[] args) {
        final Account a = new Account(1000);
        final Account b = new Account(2000);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    transfer(a, b, 500);
                } catch (InsufficientFundsException e) {
                    e.printStackTrace();
                    System.out.println(e);
                }
            }
        }).start();

        try {
            transfer(b, a, 300);
        } catch (InsufficientFundsException e) {
            e.printStackTrace();
            System.out.println(e);
        }

    }

    static void transfer(Account acc1, Account acc2, int amount) throws InsufficientFundsException {
          //BAD
//        if (acc1.getBalance() < amount)
//            throw new InsufficientFundsException();
//        synchronized (acc1) {
//            System.out.println("Get lock for Acc 1");
//            try {
//                Thread.sleep(1000);
//            } catch (InterruptedException e) {
//                System.out.println("Error Thread.sleep(1000);");
//            }
//            synchronized (acc2) {
//                System.out.println("Get lock for Acc 2");
//                acc1.withdraw(amount);
//                acc2.deposit(amount);
//                System.out.println("GOOD Transfer!");
//            }
//        }

        //GOOD
        try {
            if (acc1.getLock().tryLock(WAIT_SEC, TimeUnit.SECONDS)){
                try {
                    if (acc2.getLock().tryLock(WAIT_SEC, TimeUnit.SECONDS)){
                        try {
                            acc1.withdraw(amount);
                            acc2.deposit(amount);
                            System.out.println("GOOD Transfer!");
                        } finally {
                            acc2.getLock().unlock();
                            System.out.println("acc2.getLock().unlock();");
                        }
                    }

                } finally {
                    acc1.getLock().unlock();
                    System.out.println("acc1.getLock().unlock();");
                }
            } else {
                System.out.println("Error waiting Lock!");
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
            System.out.println(e);
        }


    }
}
