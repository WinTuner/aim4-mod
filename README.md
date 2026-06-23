# Autonomous Intersection Management 4 - Modified (aim4-mod)

โปรเจกต์นี้เป็นการนำระบบจำลองสถานการณ์จราจร **AIM4 (Autonomous Intersection Management 4)** ซึ่งพัฒนาขึ้นโดย Learning Agents Research Group จาก University of Texas at Austin มาปรับปรุงและพัฒนาต่อยอด (Modified Version) เพื่อศึกษา ทดสอบ อัลกอริทึมการจัดการพื้นที่สี่แยกสำหรับยานพาหนะอัตโนมัติไร้คนขับ (Autonomous Vehicles)

---

## 🚀 ฟีเจอร์หลัก (Key Features)
* **Reservation-Based Protocol:** ระบบจัดการสี่แยกแบบจองพื้นที่-เวลากลางอากาศ (Space-Time Reservation) ช่วยให้รถสัญจรผ่านแยกได้โดยไม่ต้องจอดรอสัญญาณไฟหากไม่มีรถคันอื่นอยู่ในเส้นทางเสี่ยงชน
* **Traffic Micro-simulation:** ตัวจำลองการจราจรระดับจุลภาคในรูปแบบ 2 มิติ ที่สามารถคำนวณตำแหน่ง ความเร็ว และอัตราเร่งของรถแต่ละคันได้อย่างละเอียด
* **Flexible Customization:** รองรับการปรับเปลี่ยนรูปแบบสี่แยก ซอย เลน และสัดส่วนของรถยนต์ประเภทต่าง ๆ (เช่น รถยนต์ไร้คนขับ 100% หรือแบบกึ่งอัตโนมัติ)

---

## 🛠️ ความต้องการของระบบ (Prerequisites)
กรุณาตรวจสอบให้แน่ใจว่าเครื่องของคุณได้ติดตั้งเครื่องมือเหล่านี้เรียบร้อยแล้วก่อนเริ่มใช้งาน:
* **Java Development Kit (JDK):** แนะนำเวอร์ชัน 8 หรือ 11 (ขึ้นอยู่กับสภาพแวดล้อมที่โค้ดส่วนนี้ถูกปรับแก้)
* **Apache Maven:** สำหรับใช้จัดการ Dependencies และสั่ง Build โปรเจกต์
* **Git:** สำหรับใช้ในการดึงโค้ดลงมาทำงาน

---

## 📦 ขั้นตอนการติดตั้งและการเปิดใช้งาน

<Sequence>
{/* Reason: การติดตั้งและการ Build โปรเจกต์ Java/Maven จำเป็นต้องทำตามลำดับขั้นตอนอย่างเคร่งครัดเพื่อป้องกันข้อผิดพลาดเรื่อง Dependency */}
  <Step title="Clone Repository" subtitle="ดาวน์โหลดซอร์สโค้ด">
    เปิด Terminal หรือ Command Prompt บนเครื่องของคุณแล้วรันคำสั่ง:
```bash
    git clone [https://github.com/WinTuner/aim4-mod.git](https://github.com/WinTuner/aim4-mod.git)
    cd aim4-mod
    ```
  </Step>
  <Step title="Build โปรเจกต์ด้วย Maven" subtitle="คอมไพล์โค้ดและรวมไฟล์">
    ดาวน์โหลดไลบรารีที่จำเป็นและแพ็กเกจตัวโปรเจกต์ให้กลายเป็นไฟล์ `.jar`:
```bash
    mvn clean package -Dmaven.test.skip=true
    ```
    *(หมายเหตุ: หากโปรเจกต์เดิมใช้รูปแบบเก่า สามารถเปลี่ยนไปใช้คำสั่ง `mvn assembly:assembly` ได้เช่นกัน)*
  </Step>
  <Step title="รันโปรแกรมในโหมดหน้าจอ GUI" subtitle="เปิดจำลองภาพแบบ 2 มิติ">
    หากต้องการดูตัวจำลองการวิ่งของรถในรูปแบบกราฟิก 2D ให้ใช้คำสั่งรันไฟล์ที่อยู่ในโฟลเดอร์ target:
```bash
    java -jar target/AIM4-1.0-SNAPSHOT-jar-with-dependencies.jar
    ```
    *(หมายเหตุ: กรุณาแก้ไขชื่อไฟล์ `.jar` ให้ตรงกับชื่อไฟล์จริงที่ปรากฏหลังจากการ Build)*
  </Step>
  <Step title="รันในโหมด Command Line (CLI)" subtitle="สำหรับการรันเก็บข้อมูลทดลอง">
    หากต้องการรันเพื่อประมวลผลความเร็วสูงและบันทึกสถิติลงไฟล์ CSV โดยไม่ต้องเปิดหน้าจอ ให้ระบุ Main Class เช่น:
```bash
    java -cp target/AIM4-1.0-SNAPSHOT-jar-with-dependencies.jar expr.trb.TrafficSignalExpr
    ```
  </Step>
</Sequence>

---

## 🔧 สิ่งที่ปรับปรุงเพิ่มเติมในเวอร์ชันนี้ (Modifications)

* ปรับปรุงพฤติกรรมการตัดสินใจขับขี่ของรถในสถานการณ์คับขัน (Driver Agent Policy)
* แก้ไขการคำนวณระยะห่างระหว่างรถยนต์เพื่อเพิ่มความปลอดภัย (Safety Buffer Evaluation)
* อัปเดตโครงสร้างไฟล์ในการบันทึกข้อมูลผลลัพธ์ (Data Export Log)

---

## 📂 โครงสร้างไดเรกทอรีสำคัญ
* `src/main/java/aim4/im/` - โค้ดส่วนควบคุมการตัดสินใจของสี่แยก (Intersection Manager)
* `src/main/java/aim4/driver/` - ตัวควบคุมพฤติกรรมและการจองพื้นที่ของฝั่งรถยนต์ (Driver Agents)
* `src/main/java/aim4/gui/` - โค้ดที่ดูแลเรื่องหน้าต่างกราฟิกและการแสดงผล 2D
* `src/main/java/expr/` - โค้ดสำหรับเซ็ตติ้งชุดข้อมูลการทดลองผ่าน Command Line

---

## 📄 ใบอนุญาต (License)
โปรเจกต์นี้อ้างอิงและพัฒนาต่อยอดมาจากซอร์สโค้ดของ AIM4 ซึ่งเผยแพร่ภายใต้ใบอนุญาต **GNU General Public License v3.0 (GPL-3.0)**