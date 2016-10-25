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

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;
import java.util.function.Consumer;

import static java.lang.String.format;

/**
 * Created by admin on 24.10.2016.
 */
public class MainConsole {

    static Scanner input = new Scanner(System.in);

    private TaskService taskService;
    private Consumer<Task> showTask = task -> System.out.printf("|%-6s|%-45s|%-14s|%-24s|%-10s\n", task.getId(), task.getTitle(), task.getPriority(), task.getDeadLine(), task.isExpired() ? "+" : "");

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
                showTaskListMenu();
                break;
            default:
                menu();
                break;
        }
    }

    private void showTaskListMenu() {
        System.out.println("-----------Tasks---------");
        showTableHead();
        taskService.findAll().forEach(showTask);
        showTableFooter();
        System.out.println("1 - Close task");
        System.out.println("2 - Show closed tasks");
        System.out.println("3 - Main menu");
        int selection = input.nextInt();
        switch (selection){
            case 3:
                menu();
                break;
            case 1:
                closeTaskMenu();
                break;
            case 2:
                showClosedTaskListMenu();
                break;
        }
    }

    private void showClosedTaskListMenu() {
        System.out.println("----Executed tasks---------");
        showTableHead();
        taskService.findAllExecuted().forEach(showTask);
        showTableFooter();
        System.out.println("3 - Main menu");
        int selection = input.nextInt();
        switch (selection){
            case 3:
                menu();
                break;
        }
    }

    private void closeTaskMenu() {
        System.out.println("Set task as done[task id]:");
        Integer doneTaskId  = input.nextInt();

        try {
            taskService.execute(doneTaskId);
        }catch (ServiceException e){
            System.out.println(format("Couldn't close task: %s", e.getMessage()));
        }
        showTaskListMenu();
    }

    private void showTableHead(){
        System.out.println("--------------------------------------------------------------------------------------------------------");
        System.out.println("|  ID  |          TITLE                              |   PRIORITY   |        DEADLINE        |  EXPIRED ");
        System.out.println("--------------------------------------------------------------------------------------------------------");
    }

    private void showTableFooter(){
        System.out.println("--------------------------------------------------------------------------------------------------------");
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
        try {
            this.taskService.save(task);
        }catch (ServiceException e){
            System.out.println(format("Couldn't save task: %s", e.getMessage()));
        }
        menu();
    }

}
