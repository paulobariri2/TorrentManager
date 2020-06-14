package torrent.manager.deamon;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class DownloadDeamon implements ApplicationRunner {

    @Override
    public void run(ApplicationArguments args) throws Exception {
        // TODO Auto-generated method stub
        while (true) {
            try {
                System.out.println("Deamon running" + Thread.currentThread().getId());
                Thread.sleep(10000L);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
    
}