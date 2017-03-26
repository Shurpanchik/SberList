import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by таня on 26.03.2017.
 */
public class SberMain {
    public static void main(String args[]) {
        System.out.println("Welcome to the world of Java");
        Scanner in = new Scanner(System.in);
        Console console = new Console(config());

        boolean isFile = false;
        try {
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(
                            new FileInputStream("C:\\Users\\таня\\Documents\\NetBeansProjects\\SberList\\src\\main\\java\\config.txt")));
            String str=reader.readLine();
            isFile =Boolean.parseBoolean(str);
            if( isFile == true) {
                String path = reader.readLine();
                reader.close();
                BufferedReader reader2 = new BufferedReader(
                        new InputStreamReader(
                                new FileInputStream(path)));
                String command = reader2.readLine();
                boolean prevList = false;
                while (command!=null || !command.toUpperCase().equals("END")) {
                    System.out.println(command);
                    console.run(command);
                    command =  reader2.readLine();
                    if(prevList==true)
                    {
                        System.out.println();
                    }
                    if(command.split(" ")[0].toUpperCase().equals("LIST")) {
                        if(prevList==false) {
                            System.out.println();
                            prevList = true;
                        }
                    }
                    else {
                        prevList = false;
                    }
                }
            }else {
                String command = in.nextLine();
                while (!command.toUpperCase().equals("END")) {
                    console.run(command);
                    command = in.nextLine();
                }
            }
        }
        catch (Exception ex)
        {
            if(isFile)
            {
                return;
            }
        }
    }

    public static List<Command> config() {
        List<Command> list = new ArrayList<>();
        list.add(new Command("LIST") {
            @Override
            public void apply(List<Integer> list, String params) {
                if (params == null || params.equals("")) {
                    params = " ";
                }
                for (int i = 0; i < list.size() - 1; i++) {
                    System.out.print(list.get(i) + params);
                }
                System.out.print(list.get(list.size() - 1));
            }
        });
        list.add(new Command("ADD") {
            @Override
            public void apply(List<Integer> list, String params) {
                list.add(Integer.parseInt(params));
            }
        });
        list.add(new Command("CLEAR") {
            @Override
            public void apply(List<Integer> list, String params) {
                list.clear();
            }
        });
        list.add(new Command("DEL") {
            @Override
            public void apply(List<Integer> list, String params) {
                String split[] = params.split(" ");
                switch (split.length) {
                    case 2:
                        for (int i = 0; i <= Integer.parseInt(split[1]) - Integer.parseInt(split[1]); i++) {
                            list.remove(Integer.parseInt(split[0]));
                        }
                    case 1:
                        list.remove(Integer.parseInt(split[0]));
                        break;
                    default:
                        break; // здесь ошибка
                }
            }
        });
        list.add(new Command("FIND") {
            @Override
            public void apply(List<Integer> list, String params) {
                list.indexOf(Integer.parseInt(params));
            }
        });
        list.add(new Command("SET") {
            @Override
            public void apply(List<Integer> list, String params) {
                String split[] = params.split(" ");
                list.set(Integer.parseInt(split[0]), Integer.parseInt(split[1]));

            }
        });
        list.add(new Command("GET") {
            @Override
            public void apply(List<Integer> list, String params) {
                System.out.print(list.get(Integer.parseInt(params)).toString());
            }
        });
        list.add(new Command("UNIQUE") {
            @Override
            public void apply(List<Integer> list, String params) {
                TreeSet<Integer> all = new TreeSet<Integer>();
                TreeSet<Integer> duplicate = new TreeSet<Integer>();
                for (int i = 0; i < list.size(); i++) {
                    int element = list.get(i);
                    if (all.contains(element)) {
                        duplicate.add(element);
                    } else {
                        all.add(element);
                    }
                }
                list.removeAll(duplicate);
            }
        });
        list.add(new Command("SAVE") {
            @Override
            public void apply(List<Integer> list, String params) {
                try {
                    String split[] = params.split(" ");
                    String str = " ";
                    String separator = " ";
                    if (split.length == 2) {
                        separator = split[1];
                    }

                    for (int i = 0; i < list.size() - 1; i++) {
                        str += list.get(i) + separator;
                    }
                    str += list.get(list.size() - 1);
                    FileWriter writer = new FileWriter(split[0]);
                    writer.write(str);
                    writer.flush();
                    writer.close();
                } catch (Exception ex) {

                }
            }
        });
        list.add(new Command("LOAD") {
            @Override
            public void apply(List<Integer> list, String params) {
                try {
                    String split[] = params.split(" ");
                    String separator = " ";
                    if (split.length == 2) {
                        separator = split[1];
                    }
                    BufferedReader reader = new BufferedReader(
                            new InputStreamReader(new FileInputStream(split[1])));
                    String str;
                    while ((str = reader.readLine()) != null) {
                        String array[] = str.split(separator);
                        for (int i = 0; i < array.length; i++) {
                            list.add(Integer.parseInt(array[i]));
                        }
                    }
                } catch (Exception ex) {
                    // здесь должен быть ошибка
                }
            }
        });

        list.add(new Command("COUNT") {
            @Override
            public void apply(List<Integer> list, String params) {
                System.out.print(list.size());
            }
        });
        list.add(new Command("SORT") {
            @Override
            public void apply(List<Integer> list, String params) {
               if(params==null || params.toLowerCase().equals("asc")|| params.equals("")) {
                   Collections.sort(list);
               }
               else {
                   Collections.sort(list);
                   Collections.reverse(list);
               }
            }
        });
        return list;
    }

}

abstract class Command {
    String name;

    public Command(String name) {
        this.name = name;
    }

    public void process(List<Integer> list, String params) {
        apply(list, params.trim());
    }

    public abstract void apply(List<Integer> list, String params);

    public String getName() {
        return name;
    }
}

class Console {
    public Map<String, Command> commands;
    public List<Integer> list;

    public Console(List<Command> commands) {
        list = new ArrayList<>();
        this.commands = commands.stream().collect(Collectors.toMap(
                c -> c.getName().toLowerCase(),
                Function.identity())
        );
    }

    public boolean run(String line) {
        try {
            line = line.trim();
            int firstSpace = line.indexOf(' ');
            if (firstSpace == -1) {
                firstSpace = line.length();
            }
            String commandName = line.substring(0, firstSpace).toLowerCase();
            String params = firstSpace != line.length() ? line.substring(firstSpace) : "";
            Command command = commands.get(commandName);
            command.process(list, params);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }
}

