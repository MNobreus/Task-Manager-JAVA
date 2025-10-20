
package gerenciadortarefas;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class Main {
    private JFrame frame;
    private DefaultListModel<Task> listModel;
    private JList<Task> taskJList;
    private TaskManager manager;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Main().createAndShowGUI());
    }

    private void createAndShowGUI() {
        manager = new TaskManager();
        frame = new JFrame("Gerenciador de Tarefas");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(700, 450);

        JPanel main = new JPanel(new BorderLayout(8,8));
        main.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

        listModel = new DefaultListModel<>();
        loadTasksIntoModel();

        taskJList = new JList<>(listModel);
        taskJList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane listScroll = new JScrollPane(taskJList);

        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));

        JButton addBtn = new JButton("Adicionar");
        JButton editBtn = new JButton("Editar");
        JButton delBtn = new JButton("Excluir");
        JButton toggleBtn = new JButton("Marcar/Desmarcar Concluída");
        JButton detailsBtn = new JButton("Ver Detalhes");

        addBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        editBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        delBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        toggleBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        detailsBtn.setAlignmentX(Component.CENTER_ALIGNMENT);

        rightPanel.add(addBtn);
        rightPanel.add(Box.createRigidArea(new Dimension(0,8)));
        rightPanel.add(editBtn);
        rightPanel.add(Box.createRigidArea(new Dimension(0,8)));
        rightPanel.add(delBtn);
        rightPanel.add(Box.createRigidArea(new Dimension(0,8)));
        rightPanel.add(toggleBtn);
        rightPanel.add(Box.createRigidArea(new Dimension(0,8)));
        rightPanel.add(detailsBtn);

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JTextField filterField = new JTextField(20);
        JComboBox<String> priorityFilter = new JComboBox<>(new String[]{"Todas","LOW","MEDIUM","HIGH"});
        topPanel.add(new JLabel("Pesquisar:"));
        topPanel.add(filterField);
        topPanel.add(new JLabel(" | Prioridade:"));
        topPanel.add(priorityFilter);

        main.add(topPanel, BorderLayout.NORTH);
        main.add(listScroll, BorderLayout.CENTER);
        main.add(rightPanel, BorderLayout.EAST);

        // ações
        addBtn.addActionListener(e -> openTaskDialog(null));
        editBtn.addActionListener(e -> {
            Task sel = taskJList.getSelectedValue();
            if (sel != null) openTaskDialog(sel);
            else JOptionPane.showMessageDialog(frame, "Selecione uma tarefa para editar.");
        });
        delBtn.addActionListener(e -> {
            Task sel = taskJList.getSelectedValue();
            if (sel != null) {
                int ok = JOptionPane.showConfirmDialog(frame, "Excluir tarefa?", "Confirma", JOptionPane.YES_NO_OPTION);
                if (ok == JOptionPane.YES_OPTION) {
                    manager.removeTask(sel.getId());
                    loadTasksIntoModel();
                }
            } else JOptionPane.showMessageDialog(frame, "Selecione uma tarefa para excluir.");
        });
        toggleBtn.addActionListener(e -> {
            Task sel = taskJList.getSelectedValue();
            if (sel != null) {
                manager.toggleComplete(sel.getId());
                loadTasksIntoModel();
            }
        });
        detailsBtn.addActionListener(e -> {
            Task sel = taskJList.getSelectedValue();
            if (sel != null) showDetails(sel);
        });

        // filtro ao digitar
        KeyAdapter filterListener = new KeyAdapter() {
            public void keyReleased(KeyEvent e) { applyFilter(filterField.getText(), (String)priorityFilter.getSelectedItem()); }
        };
        filterField.addKeyListener(filterListener);
        priorityFilter.addActionListener(e -> applyFilter(filterField.getText(), (String)priorityFilter.getSelectedItem()));

        frame.setContentPane(main);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private void loadTasksIntoModel() {
        listModel.clear();
        List<Task> tasks = manager.getTasks();
        tasks.forEach(listModel::addElement);
    }

    private void applyFilter(String text, String priority) {
        listModel.clear();
        manager.getTasks().stream()
                .filter(t -> t.getTitle().toLowerCase().contains(text.toLowerCase()) || (t.getDescription() != null && t.getDescription().toLowerCase().contains(text.toLowerCase())))
                .filter(t -> priority.equals("Todas") || t.getPriority().toString().equals(priority))
                .forEach(listModel::addElement);
    }

    private void openTaskDialog(Task task) {
        boolean isEdit = task != null;
        JDialog d = new JDialog(frame, isEdit ? "Editar Tarefa" : "Nova Tarefa", true);
        d.setSize(400, 350);
        d.setLayout(new BorderLayout(8,8));
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

        JTextField titleField = new JTextField();
        JTextArea descArea = new JTextArea(5,20);
        JComboBox<Task.Priority> prioBox = new JComboBox<>(Task.Priority.values());
        JTextField dueField = new JTextField();
        JCheckBox completedBox = new JCheckBox("Concluída");

        if (isEdit) {
            titleField.setText(task.getTitle());
            descArea.setText(task.getDescription());
            prioBox.setSelectedItem(task.getPriority());
            dueField.setText(task.getDueDate());
            completedBox.setSelected(task.isCompleted());
        }

        p.add(new JLabel("Título:"));
        p.add(titleField);
        p.add(new JLabel("Descrição:"));
        p.add(new JScrollPane(descArea));
        p.add(new JLabel("Prioridade:"));
        p.add(prioBox);
        p.add(new JLabel("Data (yyyy-MM-dd) - opcional:"));
        p.add(dueField);
        p.add(completedBox);

        JButton saveBtn = new JButton("Salvar");
        saveBtn.addActionListener(ev -> {
            String title = titleField.getText().trim();
            if (title.isEmpty()) { JOptionPane.showMessageDialog(d, "Título é obrigatório."); return; }
            String desc = descArea.getText().trim();
            Task.Priority pr = (Task.Priority) prioBox.getSelectedItem();
            String due = dueField.getText().trim();
            // validação simples de data
            if (!due.isEmpty()) {
                try { LocalDate.parse(due, DateTimeFormatter.ISO_LOCAL_DATE); }
                catch (Exception ex) { JOptionPane.showMessageDialog(d, "Data inválida. Use yyyy-MM-dd"); return; }
            }

            if (isEdit) {
                task.setTitle(title);
                task.setDescription(desc);
                task.setPriority(pr);
                task.setDueDate(due);
                task.setCompleted(completedBox.isSelected());
                manager.updateTask(task);
            } else {
                Task t = new Task(title, desc, pr, due);
                t.setCompleted(completedBox.isSelected());
                manager.addTask(t);
            }
            loadTasksIntoModel();
            d.dispose();
        });

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottom.add(saveBtn);

        d.add(p, BorderLayout.CENTER);
        d.add(bottom, BorderLayout.SOUTH);
        d.setLocationRelativeTo(frame);
        d.setVisible(true);
    }

    private void showDetails(Task t) {
        StringBuilder sb = new StringBuilder();
        sb.append("Título: ").append(t.getTitle()).append("\n\n");
        sb.append("Descrição: ").append(t.getDescription() == null ? "" : t.getDescription()).append("\n\n");
        sb.append("Prioridade: ").append(t.getPriority()).append("\n");
        sb.append("Data de vencimento: ").append(t.getDueDate() == null || t.getDueDate().isEmpty() ? "-" : t.getDueDate()).append("\n");
        sb.append("Concluída: ").append(t.isCompleted() ? "Sim" : "Não");
        JOptionPane.showMessageDialog(frame, sb.toString(), "Detalhes da Tarefa", JOptionPane.INFORMATION_MESSAGE);
    }

    private static final String _mnobreus_signature = "MNobreus";
    }
}
