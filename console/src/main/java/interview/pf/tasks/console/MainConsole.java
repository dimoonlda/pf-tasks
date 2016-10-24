package interview.pf.tasks.console;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import interview.pf.tasks.console.config.Config;
import interview.pf.tasks.model.Task;
import interview.pf.tasks.model.TaskPriority;
import interview.pf.tasks.services.TaskServiceImpl;
import interview.pf.tasks.services.dao.interfaces.TaskDao;
import interview.pf.tasks.services.dao.mysql.TaskDaoImpl;
import interview.pf.tasks.services.exceptions.ServiceException;
import interview.pf.tasks.services.interfaces.TaskService;

import javax.sql.DataSource;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

import static java.lang.String.format;

/**
 * Created by admin on 24.10.2016.
 */
public class MainConsole {

    static Scanner input = new Scanner(System.in);

    private TaskService taskService;

    public MainConsole(){
        MysqlDataSource mySqlDs = new MysqlDataSource();
        mySqlDs.setURL(Config.getInstance().getDbUrl());
        mySqlDs.setUser(Config.getInstance().getUserName());
        mySqlDs.setPassword(Config.getInstance().getPassword());
        TaskDao taskDao = new TaskDaoImpl(mySqlDs);
        this.taskService = new TaskServiceImpl(taskDao);
    }

    public static void main(String[] args) {
        if (args.length < 1){
            System.out.println("Usage: java -jar tasks.jar path-to-properties-file");
            System.exit(0);
        }
        Config config;
        try {
            config = Config.getInstance().init(args[0]);
            System.out.println("Config: " + config);
        } catch (IOException e) {
            System.out.println("Couldn't read properties from file.");
            System.exit(0);
        }
        MainConsole mainConsole = new MainConsole();
        mainConsole.menu();
    }

    private void menu() {
        int selection;
        System.out.println("Choose from these choices");
        System.out.println("-------------------------\n");
        System.out.println("1 - Add task");
        System.out.println("2 - Show task's list");
        System.out.println("3 - Quit");

        selection = input.nextInt();
        switch (selection){
            case 3:
                System.exit(0);
                break;
            case 1:
                addTaskMenu();
                break;
            case 2:
                break;
            default:
                menu();
                break;
        }
    }

    private void addTaskMenu(){
        System.out.println("---------New task--------");
        System.out.println("Title: ");
        input.nextLine();
        String title = input.nextLine();
        System.out.println("Dead-line[yyyy-mm-dd hh:mm:ss]: ");
        String deadLineStr = input.nextLine();
        System.out.println("Priority[high - 3, medium - 2, low - 1]: ");
        int priority  = input.nextInt();
        Task task = new Task()
                .setPriority(TaskPriority.valueOf(priority))
                .setTitle(title)
                .setDeadLine(LocalDateTime.parse(deadLineStr, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        System.out.println("Task: " + task);
        try {
            this.taskService.save(task);
        }catch (ServiceException e){
            System.out.println(format("Couldn't save task: %s", e.getMessage()));
        }
        menu();
    }

}
