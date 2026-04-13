/**
 * QR Scanner Module
 * Uses html5-qrcode library
 */

let html5QrCode = null;
let isFlashOn = false;

// Initialize scanner
function initScanner() {
  html5QrCode = new Html5Qrcode("reader");
  
  const config = {
    fps: 10,
    qrbox: { width: 250, height: 250 },
    aspectRatio: 1.0
  };
  
  // Request camera
  html5QrCode.start(
    { facingMode: "environment" }, // Use back camera
    config,
    onScanSuccess,
    onScanError
  ).catch(err => {
    console.error("Failed to start scanner", err);
    updateStatus("❌ Ошибка камеры", "error");
    showManualInput();
  });
}

// Success callback
function onScanSuccess(decodedText, decodedResult) {
  console.log('QR scanned:', decodedText);
  
  // Haptic feedback
  window.LoyaltyBot.hapticFeedback('success');
  
  // Parse QR data (expecting userCouponId)
  const userCouponId = parseInt(decodedText);
  
  if (isNaN(userCouponId)) {
    updateStatus("❌ Неверный формат QR", "error");
    window.LoyaltyBot.hapticFeedback('error');
    return;
  }
  
  // Stop scanner
  stopScanner();
  
  // Fetch coupon info
  fetchCouponInfo(userCouponId);
}

// Error callback
function onScanError(error) {
  // Ignore scan errors (too frequent)
  // console.warn('Scan error:', error);
}

// Stop scanner
function stopScanner() {
  if (html5QrCode) {
    html5QrCode.stop().then(() => {
      console.log('Scanner stopped');
    }).catch(err => {
      console.error('Failed to stop scanner', err);
    });
  }
}

// Fetch coupon info
async function fetchCouponInfo(userCouponId) {
  updateStatus("⏳ Загрузка информации...", "loading");
  
  try {
    const couponInfo = await window.LoyaltyBot.apiCall(`/coupons/${userCouponId}`);
    
    // Show confirmation
    showConfirmation(couponInfo);
    
  } catch (error) {
    console.error('Failed to fetch coupon info', error);
    updateStatus("❌ Купон не найден", "error");
    window.LoyaltyBot.hapticFeedback('error');
    
    // Show retry button
    setTimeout(() => {
      initScanner();
      updateStatus("Наведите камеру на QR-код", "loading");
    }, 2000);
  }
}

// Show confirmation screen
function showConfirmation(couponInfo) {
  const statusEl = document.getElementById('scanStatus');
  statusEl.className = 'status success';
  statusEl.innerHTML = `
    <div class="status-icon">✅</div>
    <div class="status-text">
      <strong>${couponInfo.couponName}</strong><br>
      Клиент: ${couponInfo.userName}<br>
      Прогресс: ${couponInfo.stamps}/${couponInfo.stampTarget}
    </div>
  `;
  
  // Show MainButton
  window.LoyaltyBot.showMainButton('✅ Добавить печать', () => {
    addStamp(couponInfo.userCouponId);
  });
  
  // Show back button
  tg.BackButton.show();
  tg.BackButton.onClick(() => {
    location.reload();
  });
}

// Add stamp
async function addStamp(userCouponId) {
  try {
    const response = await window.LoyaltyBot.apiCall('/stamps/add', {
      method: 'POST',
      body: JSON.stringify({ userCouponId })
    });
    
    // Success
    updateStatus("✅ Печать добавлена!", "success");
    window.LoyaltyBot.hapticFeedback('success');
    
    // Send data to bot
    window.LoyaltyBot.sendDataToBot({
      action: 'stamp_added',
      userCouponId,
      newStamps: response.newStamps,
      stampTarget: response.stampTarget
    });
    
    // Close after delay
    setTimeout(() => {
      tg.close();
    }, 1500);
    
  } catch (error) {
    console.error('Failed to add stamp', error);
    updateStatus("❌ Ошибка добавления", "error");
    window.LoyaltyBot.hapticFeedback('error');
  }
}

// Manual input
function addStampManual() {
  const input = document.getElementById('manualCouponId');
  const userCouponId = parseInt(input.value);
  
  if (isNaN(userCouponId)) {
    alert('Введите корректный ID');
    return;
  }
  
  stopScanner();
  fetchCouponInfo(userCouponId);
}

// Toggle flash
function toggleFlash() {
  if (!html5QrCode) return;
  
  html5QrCode.applyVideoConstraints({
    advanced: [{
      torch: isFlashOn ? false : true
    }]
  }).then(() => {
    isFlashOn = !isFlashOn;
    document.getElementById('flashIcon').textContent = isFlashOn ? '💡' : '🔦';
    document.getElementById('flashText').textContent = isFlashOn ? 'Выключить фонарик' : 'Включить фонарик';
  }).catch(err => {
    console.error('Failed to toggle flash', err);
    alert('Фонарик не поддерживается на этом устройстве');
  });
}

// Update status
function updateStatus(text, type = 'loading') {
  const statusEl = document.getElementById('scanStatus');
  statusEl.className = `status ${type}`;
  
  const icon = type === 'success' ? '✅' : type === 'error' ? '❌' : '⏳';
  statusEl.innerHTML = `
    <div class="status-icon">${icon}</div>
    <div class="status-text">${text}</div>
  `;
}

// Show manual input
function showManualInput() {
  document.querySelector('.manual-input').classList.remove('hidden');
}

// Initialize on load
document.addEventListener('DOMContentLoaded', () => {
  initScanner();
});

// Cleanup on unload
window.addEventListener('beforeunload', () => {
  stopScanner();
});
