package com.homestay.component;

import com.homestay.entity.Order;
import com.homestay.entity.Room;
import com.homestay.entity.User;
import com.homestay.repository.OrderRepository;
import com.homestay.repository.RoomRepository;
import com.homestay.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final RoomRepository roomRepository;
    private final OrderRepository orderRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        if (userRepository.count() > 0) {
            log.info("数据已存在，跳过初始化...");
            return;
        }

        log.info("开始初始化数据...");

        // 1. 初始化用户
        initUsers();

        // 2. 初始化房源
        initRooms();

        // 3. 初始化订单
        initOrders();

        log.info("数据初始化完成！");
    }

    private void initUsers() {
        // 管理员
        User admin = new User();
        admin.setUsername("admin");
        admin.setPassword(passwordEncoder.encode("admin123"));
        admin.setRole("ADMIN");
        admin.setNickname("超级管理员");
        admin.setPhone("13800138000");
        userRepository.save(admin);

        // 房东
        User host = new User();
        host.setUsername("host");
        host.setPassword(passwordEncoder.encode("123456"));
        host.setRole("HOST");
        host.setNickname("金牌房东");
        host.setPhone("13900139000");
        userRepository.save(host);

        // 普通用户
        for (int i = 1; i <= 20; i++) {
            User user = new User();
            user.setUsername("user" + i);
            user.setPassword(passwordEncoder.encode("123456"));
            user.setRole("GUEST");
            user.setNickname("游客" + i);
            user.setPhone("1370000" + String.format("%04d", i));
            user.setAvatar("https://api.dicebear.com/7.x/avataaars/svg?seed=" + i);
            userRepository.save(user);
        }
        log.info("用户数据初始化完成");
    }

    private void initRooms() {
        String[] cities = {"北京", "上海", "成都", "杭州", "大理", "三亚"};
        String[] titles = {
            "市中心温馨一居室", "海景豪华套房", "山间静谧木屋", "古镇特色客栈", 
            "现代简约Loft", "亲子主题乐园房", "商务精英公寓", "浪漫情侣圆床房"
        };
        String[] images = {
            "https://images.unsplash.com/photo-1522708323590-d24dbb6b0267?ixlib=rb-4.0.3&auto=format&fit=crop&w=2340&q=80",
            "https://images.unsplash.com/photo-1502672260266-1c1ef2d93688?ixlib=rb-4.0.3&auto=format&fit=crop&w=2340&q=80",
            "https://images.unsplash.com/photo-1560448204-e02f11c3d0e2?ixlib=rb-4.0.3&auto=format&fit=crop&w=2340&q=80",
            "https://images.unsplash.com/photo-1484154218962-a1c002085d2f?ixlib=rb-4.0.3&auto=format&fit=crop&w=2340&q=80",
            "https://images.unsplash.com/photo-1554995207-c18c203602cb?ixlib=rb-4.0.3&auto=format&fit=crop&w=2340&q=80"
        };
        List<String> facilities = Arrays.asList("WiFi", "空调", "热水", "厨房", "停车位", "电视");

        Random random = new Random();

        for (int i = 1; i <= 20; i++) {
            Room room = new Room();
            String city = cities[random.nextInt(cities.length)];
            room.setName(city + "·" + titles[random.nextInt(titles.length)]);
            room.setDescription("这就一个非常棒的房间，位于" + city + "核心地段，交通便利，设施齐全。适合情侣、家庭出游。享受宁静的夜晚，感受城市的脉搏。");
            room.setCity(city);
            room.setAddress(city + "市中心xx路" + random.nextInt(999) + "号");
            room.setBasePrice(new BigDecimal(200 + random.nextInt(800)));
            room.setCapacity(1 + random.nextInt(4));
            room.setStatus(1); // 上架
            
            // 随机分配图片
            room.setImages(Arrays.asList(images[random.nextInt(images.length)], images[random.nextInt(images.length)]));
            room.setFacilities(facilities);
            
            roomRepository.save(room);
        }
        log.info("房源数据初始化完成");
    }

    private void initOrders() {
        List<User> users = userRepository.findAll();
        List<Room> rooms = roomRepository.findAll();
        Random random = new Random();

        // 过滤掉管理员和房东，只保留GUEST
        List<User> guests = users.stream().filter(u -> "GUEST".equals(u.getRole())).toList();

        if (guests.isEmpty() || rooms.isEmpty()) return;

        for (int i = 0; i < 30; i++) {
            User guest = guests.get(random.nextInt(guests.size()));
            Room room = rooms.get(random.nextInt(rooms.size()));

            Order order = new Order();
            order.setOrderNo(UUID.randomUUID().toString().replace("-", ""));
            order.setUserId(guest.getId());
            order.setRoomId(room.getId());
            
            // 随机日期：过去30天到未来30天
            LocalDate now = LocalDate.now();
            LocalDate checkIn = now.plusDays(random.nextInt(60) - 30);
            LocalDate checkOut = checkIn.plusDays(1 + random.nextInt(5));
            
            order.setCheckInDate(checkIn);
            order.setCheckOutDate(checkOut);
            order.setGuestCount(1 + random.nextInt(room.getCapacity()));
            
            long days = checkOut.toEpochDay() - checkIn.toEpochDay();
            order.setTotalAmount(room.getBasePrice().multiply(BigDecimal.valueOf(days)));
            
            // 状态逻辑
            if (checkOut.isBefore(now)) {
                order.setStatus(3); // 已完成
            } else if (checkIn.isAfter(now)) {
                int statusRandom = random.nextInt(10);
                if (statusRandom < 2) order.setStatus(0); // 待支付
                else if (statusRandom < 8) order.setStatus(1); // 已支付
                else order.setStatus(4); // 已取消
            } else {
                order.setStatus(2); // 已入住
            }
            
            if (order.getStatus() != 0 && order.getStatus() != 4) {
                order.setPayTime(LocalDateTime.now().minusDays(random.nextInt(10)));
            }

            orderRepository.save(order);
        }
        log.info("订单数据初始化完成");
    }
}
