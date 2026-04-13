/**
 * Telegram WebApp Initialization
 */
const tg = window.Telegram.WebApp;

// Initialize Telegram WebApp
function initTelegram() {
  tg.ready();
  tg.expand(); // Expand to full height
  
  // Set header color
  tg.setHeaderColor(getComputedStyle(document.documentElement)
    .getPropertyValue('--tg-theme-bg-color'));
  
  // Enable confirmation dialog
  tg.enableClosingConfirmation();
  
  console.log('Telegram WebApp initialized');
  console.log('User:', tg.initDataUnsafe?.user);
}

// Get auth data from Telegram
function getAuthData() {
  const initData = tg.initData;
  const user = tg.initDataUnsafe?.user;
  
  if (!initData) {
    console.error('No Telegram initData found');
    return null;
  }
  
  return {
    initData,
    userId: user?.id,
    username: user?.username,
    firstName: user?.first_name,
    lastName: user?.last_name
  };
}

// Send data back to bot
function sendDataToBot(data) {
  tg.sendData(JSON.stringify(data));
  console.log('Sent to bot:', data);
}

// Show MainButton
function showMainButton(text, callback) {
  tg.MainButton.setText(text);
  tg.MainButton.show();
  tg.MainButton.onClick(() => {
    callback();
    tg.MainButton.offClick();
  });
}

// Hide MainButton
function hideMainButton() {
  tg.MainButton.hide();
  tg.MainButton.offClick();
}

// Haptic feedback
function hapticFeedback(type = 'impact') {
  if (tg.HapticFeedback) {
    if (type === 'impact') {
      tg.HapticFeedback.impactOccurred('light');
    } else if (type === 'success') {
      tg.HapticFeedback.notificationOccurred('success');
    } else if (type === 'error') {
      tg.HapticFeedback.notificationOccurred('error');
    }
  }
}

// Format date
function formatDate(dateString) {
  const date = new Date(dateString);
  const now = new Date();
  const diff = now - date;
  
  const minutes = Math.floor(diff / 60000);
  const hours = Math.floor(diff / 3600000);
  const days = Math.floor(diff / 86400000);
  
  if (minutes < 1) return 'Только что';
  if (minutes < 60) return `${minutes} мин назад`;
  if (hours < 24) return `${hours} ч назад`;
  if (days < 7) return `${days} дн назад`;
  
  return date.toLocaleDateString('ru-RU', {
    day: 'numeric',
    month: 'short'
  });
}

// API helper
async function apiCall(endpoint, options = {}) {
  const authData = getAuthData();
  
  const defaultOptions = {
    headers: {
      'Content-Type': 'application/json',
      'X-Telegram-Init-Data': authData?.initData || ''
    }
  };
  
  const response = await fetch(`/api${endpoint}`, {
    ...defaultOptions,
    ...options
  });
  
  if (!response.ok) {
    throw new Error(`API error: ${response.status}`);
  }
  
  return response.json();
}

// Initialize on load
document.addEventListener('DOMContentLoaded', () => {
  initTelegram();
});

// Export for use in other modules
window.LoyaltyBot = {
  tg,
  initTelegram,
  getAuthData,
  sendDataToBot,
  showMainButton,
  hideMainButton,
  hapticFeedback,
  formatDate,
  apiCall
};
