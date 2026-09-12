package org.njupt.njuptphysim.test;


import com.alibaba.druid.sql.visitor.functions.Char;
import com.sun.source.tree.Tree;
import org.njupt.njuptphysim.pojo.Result;
import org.njupt.njuptphysim.server.controller.stu.StuController;

import java.time.LocalDate;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;


public class Test {

    private int[] ints;


     public static class TreeNode {
     int val;
     TreeNode left;
     TreeNode right;
     TreeNode() {}
     TreeNode(int val) { this.val = val; }
     TreeNode(int val, TreeNode left, TreeNode right) {
         this.val = val;
         this.left = left;
         this.right = right;
        }
     }

    public static class ListNode {
        int val;
        ListNode next;
        ListNode() {}
        ListNode(int x) {
            val = x;
            next = null;
        }
    }


    public int digitFrequencyScore(int n) {
        HashMap<Integer,Integer> map = new HashMap<>();
        while (n>0) {
            int num = n%10;
            if (map.containsKey(num)){
                map.put(num,map.get(num)+1);
            } else {
                map.put(num,1);
            }
            n/=10;
        }

        return map.entrySet().stream()
                .mapToInt(entry -> entry.getValue()*entry.getKey())
                .sum();
    }


    public int totalWaviness(int num1, int num2) {
        if (num2<100) return 0;
        int start = Math.max(num1, 100);
        int sum = 0;
        for (int i = start; i <= num2; i++) {
            sum += solve(i);
        }
        return sum;
    }

    public int solve(int num){
        char[] array = String.valueOf(num).toCharArray();
        int ans = 0;
        for (int i = 1; i < array.length - 1; i++) {
            if (array[i] > Math.max(array[i-1],array[i+1]) || array[i] < Math.min(array[i-1], array[i+1])) {
                ans++;
            }
        }
        return ans;
    }



    public static boolean consecutiveSetBits(int n) {
        char[] chars = Integer.toBinaryString(n).toCharArray();
        int flag = 0;
        for (int i = 0; i < chars.length-1; i++) {
            if (chars[i]=='1' && chars[i+1]=='1') {
                if (flag == 1) {
                    flag = 0;
                    break;
                }
                flag = 1;
            }
        }
        return flag == 1;
    }


    private static final String[] list = {"0","1","2","3","4","5","6","7","8","9","a","b","c","d","e","f"};
    public String toHex(int num) {
        long curNum = num & 0xFFFFFFFFL;
        StringBuilder builder = new StringBuilder();
        while (curNum > 0) {
            builder.append(list[(int) (curNum % 16)]);
            curNum /= 16;
        }
        return builder.reverse().toString();
    }

    public int countSegments(String s) {
        int ans = 0, count = 0;
        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) == ' ') {
                if (count > 1) {
                    ans++;
                }
                count = 0;
            } else {
                count++;
            }
        }
        if (count>1) ans++;
        return ans;
    }


    public int maxBuilding(int n, int[][] restrictions) {
        int len = restrictions.length;
        if (len == 0) return n-1;
        int[] h = new int[len];
        Arrays.sort(restrictions, Comparator.comparingInt(a -> a[0]));
        h[0] = Math.min(restrictions[0][0]-1, restrictions[0][1]);
        for (int i = 1; i < len; i++) {
            h[i] = Math.min(restrictions[i][1], restrictions[i][0] - restrictions[i-1][0] + h[i-1]);
        }
        for (int i = len-2; i >= 0; i--) {
            h[i] = Math.min(h[i], restrictions[i+1][0] - restrictions[i][0] + h[i+1]);
        }

        int ans = Math.max((restrictions[0][0] + h[0])/2, len - restrictions[len-1][0] + h[len-1]);
        for (int i = 1; i < len; i++) {
            ans = Math.max(ans, (h[i] + h[i-1] + restrictions[i][0] - restrictions[i-1][0])/2);
        }
        return ans;
    }

    public int minLights(int[] lights) {
        int n = lights.length;
        int[] diff = new int[n + 1];
        for (int i = 0; i < n; i++) {
            if (lights[i] > 0) {
                int left = Math.max(0, i - lights[i]);
                int right = Math.min(n - 1, i + lights[i]);
                diff[left]++;
                diff[right + 1]--;
            }
        }

        int ans = 0;
        int cur = 0;
        int unlitLen = 0;
        for (int i = 0; i < n; i++) {
            cur += diff[i];
            if (cur > 0) {
                ans += (unlitLen + 2) / 3;
                unlitLen = 0;
            } else {
                unlitLen++;
            }
        }
        ans += (unlitLen + 2) / 3;
        return ans;
    }

    public int zigZagArrays(int n, int l, int r) {
        final int MOD = 1000000007;
        int k = r - l + 1;
        int[] f0 = new int[k];
        int[] f1 = new int[k];
        long[] s0 = new long[k+1];
        long[] s1 = new long[k+1];

        Arrays.fill(f0,1);
        Arrays.fill(f1,1);

        for (int i = 2; i < n; i++){
            for (int j = 0; j < k; j++) {
                s0[j+1] = s0[j] + f0[j];
                s1[j+1] = s1[j] + f1[j];
            }
            for (int j = 0; j < k; j++) {
                f1[j] = (int) (s0[k] - s0[j+1]) % MOD;
                f0[j] = (int) s1[j] & MOD;
            }
        }
        int ans = 0;
        for (int i = 0; i < k; i++) {
            ans += f0[i] + f1[i];
        }
        return ans;
    }

    public int combinationSum4(int[] nums, int target) {
        int[] memo = new int[target+1];
        Arrays.fill(memo, -1);
        return dfs(target, nums, memo);
    }

    public int dfs(int i, int[] nums, int[] memo){
        if (i == 0) return 1;
        if (memo[i] != -1) return memo[i];
        int ans = 0;
        for (int j = 0; j < nums.length; j++) {
            if (nums[j] <= i){
                ans += dfs(i - nums[j], nums, memo);
            }
        }
        return memo[i] = ans;
    }


    public int combinationSum(int[] nums, int target) {
        int[] f = new int[target+1];
        f[0] = 1;
        for (int i = 1; i <= target; i++) {
            for (int num : nums) {
                if (num <= i){
                    f[i] += f[num];
                }
            }
        }
        return f[target];
    }

    public int change(int amount, int[] coins) {
        int n = coins.length;
        int[][] memo = new int[n][amount+1];
        for (int[] ints1 : memo) {
            Arrays.fill(ints1, -1);
        }
        return dfs2(n-1, amount, coins, memo);
    }

    public int dfs2(int i, int amount, int[] coins, int[][] memo){
        if (i < 0) return amount == 0 ? 1 : 0;
        if (memo[i][amount] != -1) return memo[i][amount];
        if (coins[i] > amount) return memo[i][amount] = dfs2(i-1, amount, coins, memo);
        return memo[i][amount] = dfs2(i-1, amount, coins, memo) + dfs2(i, amount - coins[i], coins, memo);
    }

    public ListNode sortList(ListNode head) {
        if (head == null || head.next == null) return head;
        ListNode slow = head, fast = slow.next;
        while (fast != null && fast.next != null){
            fast = fast.next.next;
            slow = slow.next;
        }
        ListNode rightSon = slow.next;
        slow.next = null;

        ListNode left = sortList(head);
        ListNode right = sortList(rightSon);
        return merge(left,right);

    }

    public ListNode merge(ListNode str1, ListNode str2){
        ListNode start = new ListNode(), cur = start;
        while (str1 != null && str2 != null){
            if (str1.val > str2.val){
                cur.next = str2;
                str2 = str2.next;
            } else {
                cur.next = str1;
                str1 = str1.next;
            }
            cur = cur.next;
        }
        cur.next = (str1 == null) ? str2 : str1;
        return start.next;
    }

    public int maxNumberOfFamilies(int n, int[][] reservedSeats) {
        int ans = 0;
        Map<Integer, Integer> map = new HashMap<>();
        for (int[] reservedSeat : reservedSeats) {
            int seat = reservedSeat[1];
            if (seat >= 2 && seat <= 9){
                map.merge(reservedSeat[0], 1 << reservedSeat[1] - 2, (a, b) -> a | b);
            }
        }
        ans += (n - map.size()) * 2;
        for (Integer value : map.values()) {
            if ((value & 0b1111) == 0 || (value & 0b111100) == 0 || (value & 0b11110000) == 0){
                ans++;
            }
        }
        return ans;
    }

    public boolean isValidSudoku(char[][] board) {
        boolean[][] row = new boolean[9][9];
        boolean[][] hei = new boolean[9][9];
        boolean[][][] box = new boolean[3][3][9];
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                char c = board[i][j];
                if (c == '.') {
                    continue;
                }
                int n = c - '1';
                if (row[i][n] || hei[j][n] || box[i/3][j/3][n]){
                    return false;
                }
                row[i][n] = hei[j][n] = box[i/3][j/3][n] = true;
            }
        }
        return true;
    }


    public int climbStairs(int n, int[] costs) {
        int length = costs.length;
        int[] ans = new int[length+3];
        ans[0] = 0;
        ans[1] = 1 + costs[0];
        ans[2] = Math.min(1+ans[1]+costs[2], 4 + costs[0]);
        for (int i = 3; i < n+1; i++) {
            ans[i] = Math.min(ans[i-3] + 9, ans[i-2] + 4);
            ans[i] = Math.min(ans[i], ans[i-1] + 1);
            ans[i] += costs[i-1];
        }
        return ans[n];
    }




    public static void main(String[] args) {
        int[][] tt = {{1,2},{2,3},{3,4},{5,6},{4,5}};
        Arrays.sort(tt,1,5, (a,b) -> Integer.compare(b[0],a[0]));
        for (int[] ints : tt) {
            System.out.println(Arrays.toString(ints));
        }

    }

}
